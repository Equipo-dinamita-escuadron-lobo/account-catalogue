package com.account_catalogue.copy.domain.enums;

/**
 * Estados posibles de un trabajo de copia.
 */
public enum CopyJobState {
    RECIBIDO,
    EN_PROCESO,
    COMPLETADO,
    COMPLETADO_CON_ADVERTENCIAS,
    FALLIDO,
    CANCELADO
}
