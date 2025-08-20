package com.account_catalogue.commons.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo de respuesta estándar para errores en la API REST.
 * Proporciona información consistente sobre errores ocurridos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Timestamp cuando ocurrió el error.
     */
    private LocalDateTime timestamp;
    
    /**
     * Código de estado HTTP.
     */
    private int status;
    
    /**
     * Descripción del tipo de error.
     */
    private String error;
    
    /**
     * Mensaje descriptivo del error.
     */
    private String message;
    
    /**
     * Código de error específico de la aplicación.
     */
    private String code;
    
    /**
     * Ruta donde ocurrió el error.
     */
    private String path;
}