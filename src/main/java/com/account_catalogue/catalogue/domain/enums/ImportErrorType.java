package com.account_catalogue.catalogue.domain.enums;

import lombok.Getter;

/**
 * Tipos de errores que pueden ocurrir durante la importación de catálogo de cuentas.
 * Clasifica los errores para facilitar su manejo y presentación al usuario.
 */
@Getter
public enum ImportErrorType {
    
    /**
     * Error en la validación de formato o contenido de campos.
     * Ejemplos: campo requerido faltante, formato de código inválido.
     */
    VALIDATION_ERROR("Error de Validación"),
    
    /**
     * Error en el formato de datos dentro del archivo.
     * Ejemplos: tipo de dato incorrecto, enum inválido.
     */
    FORMAT_ERROR("Error de Formato"),
    
    /**
     * Error de referencia a datos maestros inexistentes.
     * Ejemplos: cuenta padre no existe.
     */
    REFERENCE_ERROR("Error de Referencia"),
    
    /**
     * Error en la aplicación de reglas de negocio.
     * Ejemplos: crossing no permitido, costCenter sin Estado de Resultados.
     */
    BUSINESS_RULE_ERROR("Error de Regla de Negocio"),
    
    /**
     * Error por registro duplicado interno o con la base de datos.
     * Ejemplos: código ya existe, descripción duplicada.
     */
    DUPLICATE_ERROR("Error de Duplicado"),
    
    /**
     * Error en la jerarquía de cuentas.
     * Ejemplos: cuenta hija sin padre, código padre no existe.
     */
    HIERARCHY_ERROR("Error de Jerarquía"),
    
    /**
     * Error del sistema durante el procesamiento.
     * Ejemplos: error de base de datos, error de conexión.
     */
    SYSTEM_ERROR("Error del Sistema");

    private final String description;

    ImportErrorType(String description) {
        this.description = description;
    }

    /**
     * Verifica si el error es de tipo recuperable.
     * Los errores recuperables pueden ser corregidos por el usuario.
     * 
     * @return true si el error es recuperable
     */
    public boolean isRecoverable() {
        return this != SYSTEM_ERROR;
    }

    /**
     * Verifica si el error requiere acción del usuario.
     * 
     * @return true si requiere corrección por parte del usuario
     */
    public boolean requiresUserAction() {
        return this == VALIDATION_ERROR || 
               this == FORMAT_ERROR || 
               this == REFERENCE_ERROR || 
               this == BUSINESS_RULE_ERROR ||
               this == DUPLICATE_ERROR ||
               this == HIERARCHY_ERROR;
    }

    /**
     * Obtiene la prioridad del error para ordenamiento.
     * Menor número = mayor prioridad.
     * 
     * @return nivel de prioridad (1-7)
     */
    public int getPriority() {
        return switch (this) {
            case SYSTEM_ERROR -> 1;
            case HIERARCHY_ERROR -> 2;
            case BUSINESS_RULE_ERROR -> 3;
            case REFERENCE_ERROR -> 4;
            case VALIDATION_ERROR -> 5;
            case FORMAT_ERROR -> 6;
            case DUPLICATE_ERROR -> 7;
        };
    }
}

