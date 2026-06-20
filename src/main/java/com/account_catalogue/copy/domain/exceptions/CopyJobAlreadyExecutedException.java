package com.account_catalogue.copy.domain.exceptions;

/**
 * Se lanza cuando se intenta ejecutar un trabajo de copia que ya fue completado (idempotencia).
 */
public class CopyJobAlreadyExecutedException extends RuntimeException {

    public CopyJobAlreadyExecutedException(String idProceso, int fase) {
        super("El trabajo de copia ya fue ejecutado — idProceso=" + idProceso + ", fase=" + fase);
    }
}
