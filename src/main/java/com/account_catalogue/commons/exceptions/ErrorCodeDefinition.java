package com.account_catalogue.commons.exceptions;

/**
 * Contrato común para cualquier código de error de la aplicación.
 * Permite que múltiples catálogos (por dominio) sean usados de forma uniforme.
 */
public interface ErrorCodeDefinition {

    /**
     * Código único y estable del error.
     */
    String getCode();

    /**
     * Mensaje por defecto asociado al error.
     */
    String getMessage();
}


