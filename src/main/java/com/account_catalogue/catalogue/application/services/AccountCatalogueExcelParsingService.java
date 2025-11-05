package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import com.account_catalogue.catalogue.domain.utils.StringNormalizer;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueErrorCode;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueImportException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @brief Servicio para parseo y procesamiento de archivos Excel
 *
 * Servicio especializado en el parseo de archivos Excel para importación de catálogo de cuentas.
 * Maneja la lectura, validación de formato y conversión de datos desde Excel.
 */
@Slf4j
@Service
public class AccountCatalogueExcelParsingService {

    private static final int HEADER_ROW_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 1;

    /**
     * Parsea el archivo Excel y extrae los datos de catálogo de cuentas.
     * 
     * @param file archivo Excel a procesar
     * @param entId identificador de la empresa
     * @return resultado del parseo con datos y errores
     */
    public ExcelParsingResult parseExcelFile(MultipartFile file, String entId) {
        List<AccountCatalogueExcelData> accountsData = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();
        Map<String, Integer> columnMap = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getPhysicalNumberOfRows() == 0) {
                throw AccountCatalogueImportException.forEmptyFile(file.getOriginalFilename());
            }

            // Detectar mapa de columnas dinámicamente
            columnMap = detectColumnMapping(sheet, errors);
            if (columnMap.isEmpty()) {
                throw AccountCatalogueImportException.forInvalidExcelFile(file.getOriginalFilename(),
                        "No se pudieron detectar las columnas requeridas");
            }

            // Procesar filas de datos (empezar después de headers)
            for (int rowIndex = DATA_START_ROW_INDEX; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                AccountCatalogueExcelData accountData = parseRow(row, rowIndex + 1, entId, columnMap, errors);
                if (accountData != null) {
                    accountsData.add(accountData);
                }
            }

        } catch (IOException e) {
            throw new AccountCatalogueImportException(
                    AccountCatalogueErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error leyendo archivo Excel: " + e.getMessage(),
                    e);
        }

        return ExcelParsingResult.builder()
                .accountsData(accountsData)
                .errors(errors)
                .totalRows(accountsData.size())
                .columnMap(columnMap)
                .build();
    }

    /**
     * Detecta el mapeo de columnas basado en los encabezados del archivo.
     */
    private Map<String, Integer> detectColumnMapping(Sheet sheet, List<ImportErrorDetail> errors) {
        Row headerRow = sheet.getRow(HEADER_ROW_INDEX);
        if (headerRow == null) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(1)
                    .errorCode("MISSING_HEADERS")
                    .errorMessage("El archivo no contiene encabezados")
                    .errorType(ImportErrorType.FORMAT_ERROR)
                    .build());
            return new HashMap<>();
        }

        Map<String, Integer> columnMap = new HashMap<>();
        Set<String> foundHeaders = new HashSet<>();

        // Mapear todas las columnas encontradas
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String headerValue = cell.getStringCellValue();
                if (headerValue != null) {
                    String header = StringNormalizer.normalizeHeaderName(headerValue.trim());
                    if (header != null && !header.isEmpty()) {
                        columnMap.put(header, i);
                        foundHeaders.add(header);
                    }
                }
            }
        }

        // Validar que existan los encabezados requeridos
        for (String requiredHeader : ImportConstants.REQUIRED_HEADERS) {
            if (!foundHeaders.contains(requiredHeader)) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(1)
                        .columnName(requiredHeader)
                        .errorCode("MISSING_REQUIRED_HEADER")
                        .errorMessage("Falta el encabezado requerido: " + requiredHeader)
                        .errorType(ImportErrorType.FORMAT_ERROR)
                        .build());
            }
        }

        return columnMap;
    }


    /**
     * Parsea una fila individual del Excel.
     */
    private AccountCatalogueExcelData parseRow(Row row, int rowNumber, String entId, 
                                               Map<String, Integer> columnMap, List<ImportErrorDetail> errors) {
        try {
            AccountCatalogueExcelData.AccountCatalogueExcelDataBuilder builder = AccountCatalogueExcelData.builder()
                    .rowNumber(rowNumber)
                    .idEnterprise(entId);

            // Parsear campos básicos
            builder.code(getCellValueAsString(row, columnMap.get(ImportConstants.CODE_COLUMN)));
            builder.description(getCellValueAsString(row, columnMap.get(ImportConstants.NAME_COLUMN)));

            // Parsear enums
            builder.nature(parseNature(getCellValueAsString(row, columnMap.get(ImportConstants.NATURE_COLUMN)),
                    rowNumber, errors, columnMap));
            builder.financialStatus(parseFinancialStatus(
                    getCellValueAsString(row, columnMap.get(ImportConstants.FINANCIAL_STATUS_COLUMN)),
                    rowNumber, errors, columnMap));
            builder.classification(parseClassification(
                    getCellValueAsString(row, columnMap.get(ImportConstants.CLASSIFICATION_COLUMN)),
                    rowNumber, errors, columnMap));

            // Parsear campos opcionales booleanos
            builder.crossing(parseBooleanField(
                    getCellValueAsString(row, columnMap.get(ImportConstants.CROSSING_COLUMN))));
            builder.costCenter(parseBooleanField(
                    getCellValueAsString(row, columnMap.get(ImportConstants.COST_CENTER_COLUMN))));

            return builder.build();

        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .errorCode("ROW_PARSING_ERROR")
                    .errorMessage("Error parseando fila: " + e.getMessage())
                    .errorType(ImportErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Parsea el enum Nature desde String.
     */
    private NatureEnum parseNature(String value, int rowNumber, List<ImportErrorDetail> errors,
                                   Map<String, Integer> columnMap) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Normalizar valor de entrada (sin acentos, minúsculas)
        String normalizedInput = StringNormalizer.normalizeForComparison(value);
        
        for (NatureEnum nature : NatureEnum.values()) {
            // Normalizar valor del enum para comparación
            String normalizedEnum = StringNormalizer.normalizeForComparison(nature.getState());
            if (normalizedEnum != null && normalizedEnum.equals(normalizedInput)) {
                return nature;
            }
        }

        errors.add(createEnumError(rowNumber, ImportConstants.NATURE_COLUMN, value, 
                "Debito o Credito", columnMap));
        return null;
    }

    /**
     * Parsea el enum FinancialStatus desde String.
     * Usa comparación sin acentos para permitir variaciones con/sin tildes.
     */
    private FinancialStatusEnum parseFinancialStatus(String value, int rowNumber, List<ImportErrorDetail> errors,
                                                    Map<String, Integer> columnMap) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Normalizar valor de entrada (sin acentos, minúsculas)
        String normalizedInput = StringNormalizer.normalizeForComparison(value);
        
        for (FinancialStatusEnum status : FinancialStatusEnum.values()) {
            // Normalizar valor del enum para comparación
            String normalizedEnum = StringNormalizer.normalizeForComparison(status.getState());
            if (normalizedEnum != null && normalizedEnum.equals(normalizedInput)) {
                return status;
            }
        }

        errors.add(createEnumError(rowNumber, ImportConstants.FINANCIAL_STATUS_COLUMN, value,
                "Estado de Situacion Financiero o Estado de Resultados", columnMap));
        return null;
    }

    /**
     * Parsea el enum Classification desde String.
     * Usa comparación sin acentos para permitir variaciones con/sin tildes.
     */
    private ClassificationEnum parseClassification(String value, int rowNumber, List<ImportErrorDetail> errors,
                                                  Map<String, Integer> columnMap) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Normalizar valor de entrada (sin acentos, minúsculas)
        String normalizedInput = StringNormalizer.normalizeForComparison(value);
        
        for (ClassificationEnum classification : ClassificationEnum.values()) {
            // Normalizar valor del enum para comparación
            String normalizedEnum = StringNormalizer.normalizeForComparison(classification.getState());
            if (normalizedEnum != null && normalizedEnum.equals(normalizedInput)) {
                return classification;
            }
        }

        errors.add(createEnumError(rowNumber, ImportConstants.CLASSIFICATION_COLUMN, value,
                "una clasificación válida", columnMap));
        return null;
    }

    /**
     * Parsea un campo booleano desde String.
     * Acepta: SI/SÍ/S/TRUE/1 para true, NO/N/FALSE/0 para false.
     * Omite cualquier otro valor (retorna null).
     */
    private Boolean parseBooleanField(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        // Verificar valores TRUE
        for (String trueValue : ImportConstants.BOOLEAN_TRUE_VALUES) {
            if (trueValue.equals(normalized)) {
                return true;
            }
        }

        // Verificar valores FALSE
        for (String falseValue : ImportConstants.BOOLEAN_FALSE_VALUES) {
            if (falseValue.equals(normalized)) {
                return false;
            }
        }

        // Omitir valores no reconocidos
        return null;
    }

    /**
     * Crea un error de enum inválido.
     */
    private ImportErrorDetail createEnumError(int rowNumber, String columnName, String value,
                                            String validOptions, Map<String, Integer> columnMap) {
        Integer columnNumber = columnMap.get(columnName);
        return ImportErrorDetail.builder()
                .rowNumber(rowNumber)
                .columnNumber(columnNumber != null ? columnNumber + 1 : null)
                .columnName(columnName)
                .fieldValue(value)
                .errorCode(ImportConstants.ErrorCodes.INVALID_ENUM_VALUE)
                .errorMessage(String.format("Valor inválido '%s'. Debe ser: %s", value, validOptions))
                .errorType(ImportErrorType.FORMAT_ERROR)
                .build();
    }

    private String getCellValueAsString(Row row, Integer columnIndex) {
        if (columnIndex == null) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Para códigos numéricos, retornar sin decimales
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(row, i);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Clase que representa el resultado del parseo de Excel.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcelParsingResult {
        private List<AccountCatalogueExcelData> accountsData;
        private List<ImportErrorDetail> errors;
        private int totalRows;
        private Map<String, Integer> columnMap;
    }
}

