package com.account_catalogue.catalogue.infraestructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Propiedades de configuración para carga de archivos.
 * Permite externalizar límites y restricciones de archivos en application.yml
 */
@Data
@ConfigurationProperties(prefix = "file-upload")
public class FileUploadProperties {

    /**
     * Tamaño máximo permitido para archivos en bytes.
     */
    private Long maxSize;

    /**
     * Extensiones permitidas por tipo de archivo.
     */
    private Map<String, List<String>> allowedExtensions;

    /**
     * Tipos MIME permitidos por tipo de archivo.
     */
    private Map<String, List<String>> allowedMimeTypes;
}