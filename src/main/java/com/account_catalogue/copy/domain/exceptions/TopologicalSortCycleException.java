package com.account_catalogue.copy.domain.exceptions;

/**
 * Se lanza cuando se detecta un ciclo en la jerarquía de cuentas durante el ordenamiento topológico.
 */
public class TopologicalSortCycleException extends RuntimeException {

    public TopologicalSortCycleException() {
        super("ciclo detectado en jerarquía de cuentas");
    }
}
