package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueExportInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueTemplateData;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueExportException;
import com.account_catalogue.commons.exceptions.catalogue.ExcelValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Servicio para exportación del catálogo de cuentas a formato Excel
 *
 * Servicio para la exportación del catálogo de cuentas a formato Excel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueExportService implements IAccountCatalogueExportInputPort {

    private final AccountCatalogueExcelValidationService excelValidationService;
    private final IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;

    private static final int EXPORT_PAGE_SIZE = 1000;
    private static final String TEMPLATE_PLACEHOLDER_TEXT = "Seleccionar...";

    /**
     * @brief Genera plantilla Excel vacía con validaciones pre-aplicadas
     * @param entId ID de empresa para contexto de plantilla
     * @return archivo Excel con estructura y validaciones listas para uso
     */
    @Override
    public Resource exportAccountCatalogueTemplate(String entId) {
        try {
            byte[] templateData = generateTemplateWithValidations(entId);
            return new ByteArrayResource(templateData);
        } catch (ExcelValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar plantilla", e);
        }
    }

    /**
     * @brief Exporta catálogo completo de cuentas con datos reales y validaciones
     *
     * Recupera todas las cuentas mediante paginación, las convierte al formato de plantilla,
     * genera archivo Excel con datos poblados y validaciones aplicadas.
     * @param entId ID de empresa para filtrar cuentas
     * @param status filtro opcional por estado activo/inactivo (null = todas)
     * @return archivo Excel con datos reales y validaciones aplicadas
     */
    @Override
    public Resource exportAccountCatalogueWithValidations(String entId, Boolean status) {
        try {
            // Obtener todos los datos reales con paginación y orden jerárquico, filtrados por status
            List<AccountCatalogue> accountCatalogues = getAllAccountCataloguesWithPagination(entId, status);

            // Verificar que haya datos para exportar
            if (accountCatalogues.isEmpty()) {
                throw AccountCatalogueExportException.forNoData(status);
            }

            // Convertir a template data para compatibilidad con el método existente
            List<AccountCatalogueTemplateData> templateData = convertToTemplateData(accountCatalogues);

            byte[] excelData = generateExcelFileWithValidations(templateData);
            return new ByteArrayResource(excelData);
        } catch (AccountCatalogueExportException | ExcelValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar archivo de exportación", e);
        }
    }

    /**
     * @brief Crea archivo Excel de plantilla con ejemplos y validaciones aplicadas
     * @param entId ID de empresa para contexto de plantilla
     * @return archivo Excel con estructura y validaciones listas para uso
     * @throws IOException si ocurre un error al crear el archivo Excel
     * @throws ExcelValidationException si ocurre un error al aplicar las validaciones
     */
    private byte[] generateTemplateWithValidations(String entId) throws IOException, ExcelValidationException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Plantilla_Catalogo_Cuentas");

            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle templateStyle = createTemplateStyle(workbook);

            // Crear encabezados
            createHeaders(sheet, headerStyle, createOptionalHeaderStyle(workbook));

            // Crear filas de ejemplo con estilos
            List<AccountCatalogueTemplateData> exampleData = getHardcodedTemplateData();
            createTemplateRows(sheet, templateStyle, exampleData);

            // Aplicar validaciones de datos
            applyValidationsToTemplate(sheet);

            // Ajustar ancho de columnas
            autoSizeColumns(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * @brief Genera archivo Excel con datos reales y validaciones aplicadas
     * @param data datos de cuentas a exportar
     * @return archivo Excel con datos reales y validaciones aplicadas
     * @throws IOException si ocurre un error al crear el archivo Excel
     * @throws ExcelValidationException si ocurre un error al aplicar las validaciones
     */
    private byte[] generateExcelFileWithValidations(List<AccountCatalogueTemplateData> data) throws IOException, ExcelValidationException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Catalogo_Cuentas");

            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Crear encabezados
            createHeaders(sheet, headerStyle, createOptionalHeaderStyle(workbook));

            // Llenar datos
            fillData(sheet, data, dataStyle);

            // Aplicar validaciones de datos
            applyValidationsToDataSheet(sheet, data.size());

            // Ajustar ancho de columnas
            autoSizeColumns(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * @brief Crea estilo para celdas de encabezado en Excel
     * @param workbook libro de Excel donde crear el estilo
     * @return estilo configurado para celdas de encabezado
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    /**
     * @brief Crea estilo para celdas de plantilla en Excel
     * @param workbook libro de Excel donde crear el estilo
     * @return estilo configurado para celdas de plantilla
     */
    private CellStyle createTemplateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * @brief Crea estilo para celdas de datos en Excel
     * @param workbook libro de Excel donde crear el estilo
     * @return estilo configurado para celdas de datos
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * @brief Crea estilo para celdas de encabezados opcionales en Excel
     * @param workbook libro de Excel donde crear el estilo
     * @return estilo configurado para celdas de encabezados opcionales
     */
    private CellStyle createOptionalHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    /**
     * @brief Crea encabezados en la hoja de Excel
     * @param sheet hoja de Excel donde crear los encabezados
     * @param requiredHeaderStyle estilo para encabezados requeridos
     * @param optionalHeaderStyle estilo para encabezados opcionales
     */
    private void createHeaders(Sheet sheet, CellStyle requiredHeaderStyle, CellStyle optionalHeaderStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Código\n(Requerido)",
            "Nombre\n(Requerido)",
            "Naturaleza\n(Requerido)",
            "Estado Financiero\n(Requerido)",
            "Clasificación\n(Requerido)",
            "Cruce\n(Opcional)",
            "Centro de Costo\n(Opcional)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            if (i >= 5) { // Columnas 5 y 6 son opcionales
                cell.setCellStyle(optionalHeaderStyle);
            } else {
                cell.setCellStyle(requiredHeaderStyle);
            }
        }

        headerRow.setHeightInPoints(40);
    }

    /**
     * @brief Crea filas de ejemplo en la hoja de Excel
     * @param sheet hoja de Excel donde crear las filas de ejemplo
     * @param templateStyle estilo para celdas de plantilla
     * @param exampleData datos de ejemplo a exportar
     */
    private void createTemplateRows(Sheet sheet, CellStyle templateStyle, List<AccountCatalogueTemplateData> exampleData) {
        int rowIndex = 1;
        for (AccountCatalogueTemplateData data : exampleData) {
            Row row = sheet.createRow(rowIndex++);
            fillTemplateRow(row, data, templateStyle);
        }
    }

    /**
     * @brief Llena una fila de ejemplo en la hoja de Excel
     * @param row fila de Excel donde llenar la fila de ejemplo
     * @param data datos de ejemplo a exportar
     * @param style estilo para celdas de plantilla
     */
    private void fillTemplateRow(Row row, AccountCatalogueTemplateData data, CellStyle style) {
        int colIndex = 0;
        createTemplateCell(row, colIndex++, data.getCode(), style);
        createTemplateCell(row, colIndex++, data.getName(), style);
        createTemplateCell(row, colIndex++, data.getNature() != null ? data.getNature().getState() : TEMPLATE_PLACEHOLDER_TEXT, style);
        createTemplateCell(row, colIndex++, data.getFinancialStatus() != null ? data.getFinancialStatus().getState() : TEMPLATE_PLACEHOLDER_TEXT, style);
        createTemplateCell(row, colIndex++, data.getClassification() != null ? data.getClassification().getState() : TEMPLATE_PLACEHOLDER_TEXT, style);
        createTemplateCell(row, colIndex++, "", style);
        createTemplateCell(row, colIndex++, "", style);
    }

    /**
     * @brief Crea una celda en la hoja de Excel
     * @param row fila de Excel donde crear la celda
     * @param colIndex índice de la columna
     * @param value valor a colocar en la celda
     * @param style estilo a aplicar a la celda
     */
    private void createTemplateCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * @brief Llena las filas de datos en la hoja de Excel
     * @param sheet hoja de Excel donde llenar las filas de datos
     * @param data datos de cuentas a exportar
     * @param dataStyle estilo para celdas de datos
     */
    private void fillData(Sheet sheet, List<AccountCatalogueTemplateData> data, CellStyle dataStyle) {
        int rowIndex = 1;
        for (AccountCatalogueTemplateData item : data) {
            Row row = sheet.createRow(rowIndex++);
            fillDataRow(row, item, dataStyle);
        }
    }

    /**
     * @brief Llena una fila de datos en la hoja de Excel
     * @param row fila de Excel donde llenar la fila de datos
     * @param data datos de cuentas a exportar
     * @param style estilo para celdas de datos
     */
    private void fillDataRow(Row row, AccountCatalogueTemplateData data, CellStyle style) {
        int colIndex = 0;
        row.createCell(colIndex++).setCellValue(data.getCode());
        row.createCell(colIndex++).setCellValue(data.getName());
        row.createCell(colIndex++).setCellValue(data.getNature().getState());
        row.createCell(colIndex++).setCellValue(data.getFinancialStatus().getState());
        row.createCell(colIndex++).setCellValue(data.getClassification().getState());
        row.createCell(colIndex++).setCellValue(formatBooleanValue(data.getCruce()));
        row.createCell(colIndex++).setCellValue(formatBooleanValue(data.getCentroCosto()));

        // Aplicar estilo a todas las celdas
        for (int i = 0; i < colIndex; i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    /**
     * @brief Formatea un valor booleano como "SI" o "NO"
     * @param value valor booleano a formatear
     * @return "SI" si el valor es true, "NO" si es false, "" si es null
     */
    private String formatBooleanValue(Boolean value) {
        if (value == null) {
            return "";
        }
        return value ? "SI" : "NO";
    }

    /**
     * @brief Aplica validaciones a la hoja de Excel de plantilla
     * @param sheet hoja de Excel donde aplicar las validaciones
     * @throws ExcelValidationException si ocurre un error al aplicar las validaciones
     */
    private void applyValidationsToTemplate(Sheet sheet) throws ExcelValidationException {
        int startRow = 1; // Después del encabezado
        int endRow = 1000; // Permitir muchas filas para la plantilla

        excelValidationService.applyAccountCatalogueValidations(sheet, startRow, endRow);
    }

    /**
     * @brief Aplica validaciones a la hoja de Excel de datos
     * @param sheet hoja de Excel donde aplicar las validaciones
     * @param dataRowCount número de filas de datos en la hoja
     * @throws ExcelValidationException si ocurre un error al aplicar las validaciones
     */
    private void applyValidationsToDataSheet(Sheet sheet, int dataRowCount) throws ExcelValidationException {
        int startRow = 1; // Después del encabezado
        int endRow = Math.max(dataRowCount + 100, 1000); // Datos existentes + filas adicionales

        excelValidationService.applyAccountCatalogueValidations(sheet, startRow, endRow);
    }

    /**
     * @brief Ajusta el ancho de las columnas en la hoja de Excel
     * @param sheet hoja de Excel donde ajustar el ancho de las columnas
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
            // Asegurar un ancho mínimo
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
            // Limitar ancho máximo
            if (sheet.getColumnWidth(i) > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }


    /**
     * @brief Obtiene datos para la plantilla de Excel
     */
    private List<AccountCatalogueTemplateData> getHardcodedTemplateData() {
        return List.of(
            AccountCatalogueTemplateData.builder()
                .code("1")
                .name("Activos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11")
                .name("Efectivo y equivalentes de efectivo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.CURRENTASSETS)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1105")
                .name("Caja")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.CURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("110501")
                .name("Caja principal")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11050101")
                .name("Banco menor")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11050102")
                .name("Banco")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("110502")
                .name("Caja menor")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1106")
                .name("Reservas internacionales")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("12")
                .name("Inversiones e instrumentos derivados")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1205")
                .name("Propiedades, planta y equipo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1206")
                .name("Activos intangibles")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2")
                .name("Pasivos")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("21")
                .name("Operaciones de banca central e instituciones financieras")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2105")
                .name("Cuentas por pagar")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTASSETS)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2106")
                .name("Obligaciones financieras corrientes")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("22")
                .name("Emisión y colocación de títulos de deuda")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2205")
                .name("Obligaciones financieras no corrientes")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2206")
                .name("Beneficios a empleados a largo plazo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("3")
                .name("Patrimonio")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("31")
                .name("Capital social")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("32")
                .name("Utilidades acumuladas")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("33")
                .name("Utilidades NO acumuladas")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("4")
                .name("Ingresos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("5")
                .name("Gastos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("6")
                .name("Costos de ventas")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("7")
                .name("Costos de transformación")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("8")
                .name("Cuentas de orden deudoras")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("9")
                .name("Cuentas de orden acreedoras")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build()
        );
    }

    /**
     * @brief Recupera catálogo completo mediante paginación para evitar memory leaks
     * @param entId ID de empresa para filtrar cuentas
     * @param status filtro opcional por estado activo/inactivo (null = todas)
     * @return lista de cuentas contables
     */
    private List<AccountCatalogue> getAllAccountCataloguesWithPagination(String entId, Boolean status) {
        List<AccountCatalogue> allAccounts = new ArrayList<>();
        int currentPage = 0;
        Page<AccountCatalogue> page;

        do {
            // Crear pageable para la página actual
            Pageable pageable = PageRequest.of(currentPage, EXPORT_PAGE_SIZE);

            // Obtener página de cuentas ordenadas por código (jerarquía) filtradas por status
            page = accountCatalogueSearchInputPort.getAllAccountCataloguesByStatus(entId, status, pageable);

            // Agregar contenido de esta página a la lista total
            if (page != null && page.hasContent()) {
                allAccounts.addAll(page.getContent());
            }

            currentPage++;

        } while (page != null && page.hasNext());

        return allAccounts;
    }

    /**
     * @brief Convierte una lista de cuentas contables a datos de plantilla
     * @param accounts lista de cuentas contables a convertir
     * @return lista de datos de plantilla
     */
    private List<AccountCatalogueTemplateData> convertToTemplateData(List<AccountCatalogue> accounts) {
        return accounts.stream()
            .map(this::convertAccountToTemplateData)
            .toList();
    }

    /**
     * @brief Convierte una cuenta contable a datos de plantilla
     * @param account cuenta contable a convertir
     * @return datos de plantilla
     */
    private AccountCatalogueTemplateData convertAccountToTemplateData(AccountCatalogue account) {
        return AccountCatalogueTemplateData.builder()
            .code(account.getCode())
            .name(account.getDescription())
            .nature(account.getNature())
            .financialStatus(account.getFinancialStatus())
            .classification(account.getClassification())
            .cruce(account.getCrossing())
            .centroCosto(account.getCostCenter())
            .build();
    }

}