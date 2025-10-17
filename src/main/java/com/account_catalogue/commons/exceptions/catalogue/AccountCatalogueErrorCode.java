package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.ErrorCodeDefinition;
import lombok.Getter;

/**
 * Códigos de error específicos del dominio de Catálogo de Cuentas.
 */
@Getter
public enum AccountCatalogueErrorCode implements ErrorCodeDefinition {

    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"),
    ACCOUNT_INACTIVE("ACCOUNT_INACTIVE", "La cuenta está inactiva"),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT_ALREADY_EXISTS", "La cuenta ya existe"),
    ACCOUNT_DESCRIPTION_ALREADY_EXISTS("ACCOUNT_DESCRIPTION_ALREADY_EXISTS", "Ya existe una cuenta con esta descripción"),
    INVALID_ACCOUNT_CODE("INVALID_ACCOUNT_CODE", "Código de cuenta inválido"),
    ACCOUNT_HAS_CHILDREN("ACCOUNT_HAS_CHILDREN", "La cuenta tiene subcuentas asociadas"),
    PARENT_ACCOUNT_NOT_FOUND("PARENT_ACCOUNT_NOT_FOUND", "Cuenta padre no encontrada"),
    ACCOUNT_ASSOCIATED_WITH_TAX("ACCOUNT_ASSOCIATED_WITH_TAX", "La cuenta está asociada a uno o más impuestos"),
    INVALID_ACCOUNT_CODE_LENGTH("INVALID_ACCOUNT_CODE_LENGTH", "El código de cuenta debe tener exactamente 1, 2, 4, 6 u 8 dígitos");

    private final String code;
    private final String message;

    AccountCatalogueErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
