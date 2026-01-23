package com.account_catalogue.accounting.domain.exception;

/**
 * Excepción personalizada para errores de validación en los datos de entrada de los eventos.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
    
}
