package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio especializado en construir respuestas de importación.
 * Centraliza la lógica de determinación de estado y construcción de métricas.
 */
@Slf4j
@Service
public class AccountCatalogueImportResponseBuilder {

    /**
     * Construye una respuesta exitosa de importación.
     * 
     * @param entId identificador de la empresa
     * @param fileName nombre del archivo importado
     * @param totalRecords total de registros procesados
     * @param successCount registros importados exitosamente
     * @param failureCount registros que fallaron
     * @param duplicatesSkipped registros duplicados omitidos
     * @param errors lista de errores encontrados
     * @return respuesta de importación completa
     */
    public AccountCatalogueImportResponse buildSuccessResponse(String entId, String fileName, 
                                                               int totalRecords, int successCount, 
                                                               int failureCount, int duplicatesSkipped,
                                                               List<ImportErrorDetail> errors) {
        ImportStatus status = determineImportStatus(successCount, failureCount);

        AccountCatalogueImportResponse response = AccountCatalogueImportResponse.builder()
                .entId(entId)
                .fileName(fileName)
                .status(status)
                .totalRecords(totalRecords)
                .successfulImports(successCount)
                .failedImports(failureCount)
                .duplicatesSkipped(duplicatesSkipped)
                .errors(errors)
                .build();

        log.info("Importación completada: {} - Total: {}, Exitosos: {}, Fallidos: {}, Duplicados: {}", 
                status, totalRecords, successCount, failureCount, duplicatesSkipped);

        return response;
    }

    /**
     * Construye una respuesta de importación fallida.
     * 
     * @param entId identificador de la empresa
     * @param fileName nombre del archivo
     * @param totalRecords total de registros
     * @param errors lista de errores
     * @return respuesta indicando fallo
     */
    public AccountCatalogueImportResponse buildFailedResponse(String entId, String fileName, 
                                                              int totalRecords, List<ImportErrorDetail> errors) {
        AccountCatalogueImportResponse response = AccountCatalogueImportResponse.builder()
                .entId(entId)
                .fileName(fileName)
                .status(ImportStatus.FAILED)
                .totalRecords(totalRecords)
                .successfulImports(0)
                .failedImports(totalRecords)
                .duplicatesSkipped(0)
                .errors(errors)
                .build();

        log.error("Importación fallida: Total: {}, Errores: {}", totalRecords, errors.size());

        return response;
    }

    /**
     * Determina el estado final de la importación basado en resultados.
     * 
     * @param successCount registros exitosos
     * @param failureCount registros fallidos
     * @return estado de importación apropiado
     */
    private ImportStatus determineImportStatus(int successCount, int failureCount) {
        if (failureCount == 0 && successCount > 0) {
            return ImportStatus.COMPLETED;
        } else if (successCount > 0 && failureCount > 0) {
            return ImportStatus.COMPLETED_WITH_ERRORS;
        } else if (successCount == 0 && failureCount > 0) {
            return ImportStatus.FAILED;
        } else {
            // Caso especial: no hay registros
            return ImportStatus.FAILED;
        }
    }

    /**
     * Construye una respuesta para archivo vacío.
     */
    public AccountCatalogueImportResponse buildEmptyFileResponse(String entId, String fileName) {
        return AccountCatalogueImportResponse.builder()
                .entId(entId)
                .fileName(fileName)
                .status(ImportStatus.FAILED)
                .totalRecords(0)
                .successfulImports(0)
                .failedImports(0)
                .duplicatesSkipped(0)
                .errors(List.of(ImportErrorDetail.builder()
                        .rowNumber(1)
                        .errorCode("EMPTY_FILE")
                        .errorMessage("El archivo no contiene datos para importar")
                        .errorType(com.account_catalogue.catalogue.domain.enums.ImportErrorType.VALIDATION_ERROR)
                        .build()))
                .build();
    }
}

