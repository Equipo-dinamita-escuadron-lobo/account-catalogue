package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para solicitud de importación de catálogo de cuentas.
 * Contiene el archivo Excel y metadatos necesarios para la importación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCatalogueImportRequest {

    /**
     * Identificador de la empresa a la que pertenecen las cuentas.
     */
    @NotBlank(message = "El ID de empresa es requerido")
    private String entId;

    /**
     * Archivo Excel con los datos del catálogo de cuentas.
     */
    @NotNull(message = "El archivo Excel es requerido")
    private MultipartFile excelFile;

    /**
     * Nombre original del archivo (generalmente tomado del MultipartFile).
     */
    private String fileName;

    /**
     * Constructor de conveniencia para crear request desde parámetros del controlador.
     */
    public static AccountCatalogueImportRequest from(String entId, MultipartFile excelFile) {
        return AccountCatalogueImportRequest.builder()
                .entId(entId)
                .excelFile(excelFile)
                .fileName(excelFile != null ? excelFile.getOriginalFilename() : null)
                .build();
    }
}

