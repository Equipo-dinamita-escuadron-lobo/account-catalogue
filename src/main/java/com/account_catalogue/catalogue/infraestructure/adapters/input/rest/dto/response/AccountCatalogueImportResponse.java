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
 * @brief DTO para respuesta completa del proceso de importación Excel
 *
 * Contiene estadísticas detalladas, estado del proceso, errores encontrados
 * y metadatos del archivo procesado durante la importación masiva.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCatalogueImportResponse {

    private String entId;
    private String fileName;
    private ImportStatus status;
    private int totalRecords;
    private int successfulImports;
    private int failedImports;
    private int duplicatesSkipped;
    private List<ImportErrorDetail> errors;


}

