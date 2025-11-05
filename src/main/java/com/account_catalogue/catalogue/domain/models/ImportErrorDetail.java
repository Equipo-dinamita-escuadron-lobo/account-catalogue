package com.account_catalogue.catalogue.domain.models;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief DTO para representar errores detallados durante importación Excel
 *
 * Contiene información completa de localización y contexto de errores encontrados
 * durante el proceso de importación masiva de cuentas contables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorDetail {

    private Integer rowNumber;
    private Integer columnNumber;
    private String columnName;
    private String fieldValue;
    private String errorCode;
    private String errorMessage;
    private ImportErrorType errorType;
}

