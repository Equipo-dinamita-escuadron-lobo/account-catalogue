package com.account_catalogue.commons.exceptions;

import lombok.Getter;

/**
 * Catálogo de errores núcleo y comunes a toda la aplicación.
 */
@Getter
public enum ErrorCode implements ErrorCodeDefinition {

    GENERIC_ERROR("GENERIC_ERROR", "Ha ocurrido un error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
