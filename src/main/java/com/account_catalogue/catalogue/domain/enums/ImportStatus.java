package com.account_catalogue.catalogue.domain.enums;

import lombok.Getter;

/**
 * Estados posibles del proceso de importación de catálogo de cuentas.
 * Define los diferentes estados que puede tener una importación
 * durante su ciclo de vida.
 */
@Getter
public enum ImportStatus {
    
    /**
     * El proceso de importación está pendiente de iniciarse.
     */
    PENDING("Pendiente"),
    
    /**
     * El proceso de importación está actualmente en ejecución.
     */
    PROCESSING("Procesando"),
    
    /**
     * El proceso de importación se completó exitosamente sin errores.
     */
    COMPLETED("Completado"),
    
    /**
     * El proceso de importación se completó pero con algunos errores.
     * Algunos registros fueron procesados exitosamente.
     */
    COMPLETED_WITH_ERRORS("Completado con Errores"),
    
    /**
     * El proceso de importación falló completamente.
     * No se procesó ningún registro exitosamente.
     */
    FAILED("Fallido");

    private final String description;

    ImportStatus(String description) {
        this.description = description;
    }

    /**
     * Verifica si el estado indica que la importación ha terminado.
     * 
     * @return true si la importación ha terminado (exitosa o fallida)
     */
    public boolean isFinished() {
        return this == COMPLETED || this == COMPLETED_WITH_ERRORS || this == FAILED;
    }

    /**
     * Verifica si el estado indica éxito total o parcial.
     * 
     * @return true si hay al menos algunos registros procesados exitosamente
     */
    public boolean hasSuccessfulRecords() {
        return this == COMPLETED || this == COMPLETED_WITH_ERRORS;
    }

    /**
     * Verifica si el estado indica que hubo errores durante el procesamiento.
     * 
     * @return true si hubo errores
     */
    public boolean hasErrors() {
        return this == COMPLETED_WITH_ERRORS || this == FAILED;
    }
}

