package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.AccountCodeUtils;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @brief Servicio para validación por lotes de cuentas contables
 *
 * Realiza validaciones masivas en lotes de datos de cuentas durante
 * el proceso de importación desde archivos Excel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueBatchValidationService {

    private final AccountCatalogueValidationService validationService;

    /**
     * Valida un lote de datos de cuentas contables desde Excel.
     */
    public BatchValidationResult validateBatch(List<AccountCatalogueExcelData> accountsData, String entId,
                                               Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        List<AccountCatalogueExcelData> validRecords = new ArrayList<>();

        for (AccountCatalogueExcelData excelData : accountsData) {
            try {
                List<ImportErrorDetail> recordErrors = validateSingleRecord(excelData, columnMap);
                errors.addAll(recordErrors);

                if (recordErrors.isEmpty()) {
                    validRecords.add(excelData);
                }

            } catch (Exception e) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(excelData.getRowNumber())
                        .errorCode("VALIDATION_SYSTEM_ERROR")
                        .errorMessage("Error del sistema validando registro: " + e.getMessage())
                        .errorType(ImportErrorType.SYSTEM_ERROR)
                        .build());
            }
        }

        return BatchValidationResult.builder()
                .validRecords(validRecords)
                .errors(errors)
                .totalProcessed(accountsData.size())
                .validCount(validRecords.size())
                .errorCount(errors.size())
                .build();
    }

    /**
     * Valida un registro individual.
     */
    private List<ImportErrorDetail> validateSingleRecord(AccountCatalogueExcelData excelData,
                                                         Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();

        validateRequiredFields(excelData, errors, columnMap);

        validateCodeFormat(excelData, errors, columnMap);

        validateDescriptionFormat(excelData, errors, columnMap);

        validateCrossingField(excelData, errors, columnMap);

        validateCostCenterField(excelData, errors, columnMap);

        return errors;
    }

    /**
     * Valida que los campos requeridos estén presentes.
     * Todos son requeridos excepto crossing y costCenter.
     */
    private void validateRequiredFields(AccountCatalogueExcelData excelData, List<ImportErrorDetail> errors,
                                       Map<String, Integer> columnMap) {
        if (excelData.getCode() == null || excelData.getCode().trim().isEmpty()) {
            errors.add(createRequiredFieldError(excelData.getRowNumber(), ImportConstants.CODE_COLUMN, columnMap));
        }

        if (excelData.getDescription() == null || excelData.getDescription().trim().isEmpty()) {
            errors.add(createRequiredFieldError(excelData.getRowNumber(), ImportConstants.NAME_COLUMN, columnMap));
        }

        if (excelData.getNature() == null) {
            errors.add(createRequiredFieldError(excelData.getRowNumber(), ImportConstants.NATURE_COLUMN, columnMap));
        }

        if (excelData.getFinancialStatus() == null) {
            errors.add(createRequiredFieldError(excelData.getRowNumber(), 
                    ImportConstants.FINANCIAL_STATUS_COLUMN, columnMap));
        }

        if (excelData.getClassification() == null) {
            errors.add(createRequiredFieldError(excelData.getRowNumber(), 
                    ImportConstants.CLASSIFICATION_COLUMN, columnMap));
        }
    }

    /**
     * Valida el formato del código.
     */
    private void validateCodeFormat(AccountCatalogueExcelData excelData, List<ImportErrorDetail> errors,
                                   Map<String, Integer> columnMap) {
        String code = excelData.getCode();
        if (code == null || code.trim().isEmpty()) {
            return; // Ya validado en campos requeridos
        }

        try {
            // Validar que solo contenga dígitos
            if (!code.trim().matches("^\\d+$")) {
                errors.add(createError(excelData.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                        ImportConstants.ErrorCodes.INVALID_CODE_FORMAT,
                        "El código debe contener solo dígitos",
                        ImportErrorType.FORMAT_ERROR, columnMap));
                return;
            }

            // Validar longitud (1, 2, 4, 6 u 8)
            if (!AccountCodeUtils.isValidCodeLength(code)) {
                errors.add(createError(excelData.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                        ImportConstants.ErrorCodes.INVALID_CODE_LENGTH,
                        "El código debe tener exactamente 1, 2, 4, 6 u 8 dígitos. Actual: " + code.trim().length(),
                        ImportErrorType.VALIDATION_ERROR, columnMap));
            }

            // Validar que sea positivo
            long codeValue = Long.parseLong(code.trim());
            if (codeValue <= 0) {
                errors.add(createError(excelData.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                        ImportConstants.ErrorCodes.INVALID_CODE_FORMAT,
                        "El código debe ser un número positivo",
                        ImportErrorType.VALIDATION_ERROR, columnMap));
            }

        } catch (NumberFormatException e) {
            errors.add(createError(excelData.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                    ImportConstants.ErrorCodes.INVALID_CODE_FORMAT,
                    "El código no es un número válido",
                    ImportErrorType.FORMAT_ERROR, columnMap));
        }
    }

    /**
     * Valida el formato de la descripción.
     */
    private void validateDescriptionFormat(AccountCatalogueExcelData excelData, List<ImportErrorDetail> errors,
                                          Map<String, Integer> columnMap) {
        String description = excelData.getDescription();
        if (description == null || description.trim().isEmpty()) {
            return; // Ya validado en campos requeridos
        }

        try {
            validationService.validateAccountDescription(description);
        } catch (Exception e) {
            errors.add(createError(excelData.getRowNumber(), ImportConstants.NAME_COLUMN, description,
                    ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION,
                    e.getMessage(),
                    ImportErrorType.BUSINESS_RULE_ERROR, columnMap));
        }
    }

    
    /**
     * Valida el campo crossing.
     * Solo permitido en cuentas auxiliares (8 dígitos).
     */
    private void validateCrossingField(AccountCatalogueExcelData excelData, List<ImportErrorDetail> errors,
                                      Map<String, Integer> columnMap) {
        Boolean crossing = excelData.getCrossing();
        if (crossing == null || !crossing) {
            return; // null o false son valores válidos siempre
        }

        // Si crossing es true, debe ser cuenta auxiliar
        if (!excelData.isAuxiliaryAccount()) {
            String code = excelData.getCode();
            errors.add(createError(excelData.getRowNumber(), ImportConstants.CROSSING_COLUMN, "SI",
                    ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION,
                    String.format("El cruce solo puede establecerse en cuentas auxiliares (8 dígitos). " +
                            "Código actual: '%s' tiene %d dígitos", code, code != null ? code.trim().length() : 0),
                    ImportErrorType.BUSINESS_RULE_ERROR, columnMap));
        }
    }

    /**
     * Valida el campo costCenter.
     * Solo permitido en cuentas auxiliares con Estado de Resultados.
     */
    private void validateCostCenterField(AccountCatalogueExcelData excelData, List<ImportErrorDetail> errors,
                                        Map<String, Integer> columnMap) {
        Boolean costCenter = excelData.getCostCenter();
        if (costCenter == null || !costCenter) {
            return; // null o false son valores válidos siempre
        }

        // Si costCenter es true, debe ser cuenta auxiliar
        if (!excelData.isAuxiliaryAccount()) {
            String code = excelData.getCode();
            errors.add(createError(excelData.getRowNumber(), ImportConstants.COST_CENTER_COLUMN, "SI",
                    ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION,
                    String.format("El centro de costo solo puede establecerse en cuentas auxiliares (8 dígitos). " +
                            "Código actual: '%s' tiene %d dígitos", code, code != null ? code.trim().length() : 0),
                    ImportErrorType.BUSINESS_RULE_ERROR, columnMap));
            return;
        }

        // Si costCenter es true, el estado financiero debe ser Estado de Resultados
        if (excelData.getFinancialStatus() != FinancialStatusEnum.INCOMESTATEMENT) {
            errors.add(createError(excelData.getRowNumber(), ImportConstants.COST_CENTER_COLUMN, "SI",
                    ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION,
                    String.format("El centro de costo solo puede establecerse cuando el estado financiero " +
                            "sea 'Estado de Resultados'. Estado actual: %s",
                            excelData.getFinancialStatus() != null ? 
                                    excelData.getFinancialStatus().getState() : "No definido"),
                    ImportErrorType.BUSINESS_RULE_ERROR, columnMap));
        }
    }

    /**
     * Crea un error de campo requerido.
     */
    private ImportErrorDetail createRequiredFieldError(int rowNumber, String columnName,
                                                      Map<String, Integer> columnMap) {
        return createError(rowNumber, columnName, null,
                ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING,
                "El campo " + columnName + " es obligatorio",
                ImportErrorType.VALIDATION_ERROR,
                columnMap);
    }

    /**
     * Crea un error genérico.
     */
    private ImportErrorDetail createError(int rowNumber, String columnName, String fieldValue,
                                         String errorCode, String errorMessage,
                                         ImportErrorType errorType, Map<String, Integer> columnMap) {
        Integer columnNumber = null;
        if (columnMap != null && columnName != null) {
            columnNumber = columnMap.get(columnName);
            if (columnNumber != null) {
                columnNumber = columnNumber + ImportConstants.Defaults.COLUMN_START_INDEX;
            }
        }

        return ImportErrorDetail.builder()
                .rowNumber(rowNumber)
                .columnNumber(columnNumber)
                .columnName(columnName)
                .fieldValue(fieldValue)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorType(errorType)
                .build();
    }

    /**
     * Clase que representa el resultado de validación de un lote completo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchValidationResult {
        private List<AccountCatalogueExcelData> validRecords;
        private List<ImportErrorDetail> errors;
        private int totalProcessed;
        private int validCount;
        private int errorCount;
    }
}

