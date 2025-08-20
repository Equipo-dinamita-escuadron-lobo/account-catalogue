package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.ErrorCodeDefinition;
import lombok.Getter;

/**
 * Códigos de error específicos del dominio de Catálogo de Cuentas.
 */
@Getter
public enum AccountCatalogueErrorCode implements ErrorCodeDefinition {

    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT_ALREADY_EXISTS", "La cuenta ya existe"),
    INVALID_ACCOUNT_CODE("INVALID_ACCOUNT_CODE", "Código de cuenta inválido"),
    ACCOUNT_HAS_CHILDREN("ACCOUNT_HAS_CHILDREN", "La cuenta tiene subcuentas asociadas"),
    PARENT_ACCOUNT_NOT_FOUND("PARENT_ACCOUNT_NOT_FOUND", "Cuenta padre no encontrada"),
    INVALID_ACCOUNT_LEVEL("INVALID_ACCOUNT_LEVEL", "Nivel de cuenta inválido");

    private final String code;
    private final String message;

    AccountCatalogueErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
