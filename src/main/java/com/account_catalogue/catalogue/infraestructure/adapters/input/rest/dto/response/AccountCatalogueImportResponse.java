package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response;

import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de importación de catálogo de cuentas.
 * Contiene estadísticas, estado y errores de la importación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCatalogueImportResponse {

    /**
     * Identificador de la empresa.
     */
    private String entId;

    /**
     * Nombre del archivo procesado.
     */
    private String fileName;

    /**
     * Estado final de la importación.
     */
    private ImportStatus status;

    /**
     * Total de registros encontrados en el archivo.
     */
    private int totalRecords;

    /**
     * Número de cuentas importadas exitosamente.
     */
    private int successfulImports;

    /**
     * Número de cuentas que fallaron durante la importación.
     */
    private int failedImports;

    /**
     * Número de cuentas duplicadas que fueron omitidas.
     */
    private int duplicatesSkipped;

    /**
     * Lista detallada de errores encontrados durante la importación.
     * Solo se incluye si hay errores.
     */
    private List<ImportErrorDetail> errors;


}

