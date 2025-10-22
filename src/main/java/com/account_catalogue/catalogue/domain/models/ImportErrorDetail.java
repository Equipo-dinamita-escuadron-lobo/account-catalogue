package com.account_catalogue.catalogue.domain.models;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un error específico durante la importación.
 * Proporciona información detallada para facilitar la corrección.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorDetail {

    /**
     * Número de fila en el Excel donde ocurrió el error (base 1).
     */
    private Integer rowNumber;

    /**
     * Número de columna donde ocurrió el error (base 1).
     */
    private Integer columnNumber;

    /**
     * Nombre de la columna donde ocurrió el error.
     */
    private String columnName;

    /**
     * Valor que causó el error.
     */
    private String fieldValue;

    /**
     * Código del error para categorización.
     */
    private String errorCode;

    /**
     * Mensaje descriptivo del error.
     */
    private String errorMessage;

    /**
     * Tipo de error para clasificación.
     */
    private ImportErrorType errorType;
}

