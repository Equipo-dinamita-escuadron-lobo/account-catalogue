package com.account_catalogue.copy.domain.exceptions;

/**
 * Se lanza cuando no se encuentra un trabajo de copia con el idProceso dado.
 */
public class CopyJobNotFoundException extends RuntimeException {

    public CopyJobNotFoundException(String idProceso) {
        super("Trabajo de copia no encontrado: " + idProceso);
    }
}
