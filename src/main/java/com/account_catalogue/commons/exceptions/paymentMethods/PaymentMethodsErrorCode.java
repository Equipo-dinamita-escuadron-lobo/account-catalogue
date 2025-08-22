package com.account_catalogue.commons.exceptions.paymentMethods;

import com.account_catalogue.commons.exceptions.ErrorCodeDefinition;

import lombok.Getter;

/**
 * Códigos de error específicos del dominio de Métodos de Pago.
 */
@Getter
public enum PaymentMethodsErrorCode implements ErrorCodeDefinition {
    PAYMENT_METHOD_NOT_FOUND("PAYMENT_METHOD_NOT_FOUND", "Método de pago no encontrado"),
    PAYMENT_METHOD_ALREADY_EXISTS("PAYMENT_METHOD_ALREADY_EXISTS", "El método de pago ya existe"),
    INVALID_ACCOUNTING_ACCOUNT("INVALID_ACCOUNTING_ACCOUNT", "La cuenta contable no es válida");

    private final String code;
    private final String message;

    PaymentMethodsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
