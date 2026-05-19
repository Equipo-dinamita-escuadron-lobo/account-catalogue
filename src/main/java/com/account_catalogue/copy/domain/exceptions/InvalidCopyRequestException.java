package com.account_catalogue.copy.domain.exceptions;

/**
 * Se lanza cuando el request de copia tiene datos inválidos (p.e. entOrigen == entDestino).
 */
public class InvalidCopyRequestException extends RuntimeException {

    public InvalidCopyRequestException(String mensaje) {
        super(mensaje);
    }
}
