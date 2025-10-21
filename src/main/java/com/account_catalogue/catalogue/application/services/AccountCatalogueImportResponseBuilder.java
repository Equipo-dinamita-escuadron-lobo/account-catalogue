package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountCatalogueImportResponseBuilder {

    /**
     * Construye una respuesta exitosa de importación.
     * 
     * IMPORTANTE: 
     * - totalRecords = registros únicos leídos del Excel (sin duplicados internos)
     * - successfulImports = registros insertados exitosamente
     * - failedImports = registros con errores (contar filas únicas, no cantidad de errores)
     * - duplicatesSkipped = duplicados omitidos (en Excel y en BD)
     * - Validación: totalRecords = successfulImports + failedImports + duplicatesSkipped
     */
    public AccountCatalogueImportResponse buildSuccessResponse(String entId, String fileName,
            int totalRecords, int successCount,
            int failureCount, int duplicatesSkipped,
            List<ImportErrorDetail> errors) {
        // Calcular failedImports como filas únicas con errores (no cantidad de errores)
        long uniqueErrorRows = errors.stream()
                .map(ImportErrorDetail::getRowNumber)
                .filter(rowNum -> rowNum != null)
                .distinct()
                .count();
        
        int adjustedFailedImports = uniqueErrorRows > 0 ? (int) uniqueErrorRows : failureCount;
        
        // Determinar status considerando también los duplicados
        ImportStatus status = determineImportStatus(successCount, adjustedFailedImports, duplicatesSkipped);

        AccountCatalogueImportResponse response = AccountCatalogueImportResponse.builder()
                .entId(entId)
                .fileName(fileName)
                .status(status)
                .totalRecords(totalRecords)
                .successfulImports(successCount)
                .failedImports(adjustedFailedImports)
                .duplicatesSkipped(duplicatesSkipped)
                .errors(errors)
                .build();

        log.info("Importación completada - Total: {}, Exitosos: {}, Fallidos: {}, Duplicados: {}, Status: {}", 
                totalRecords, successCount, adjustedFailedImports, duplicatesSkipped, status);

        return response;
    }

    public AccountCatalogueImportResponse buildFailedResponse(String entId, String fileName,
            int totalRecords, List<ImportErrorDetail> errors) {
        // Calcular registros fallidos: contar filas únicas con errores
        long failedRecordsCount = errors.stream()
                .map(ImportErrorDetail::getRowNumber)
                .filter(rowNum -> rowNum != null)
                .distinct()
                .count();

        int failedImports = failedRecordsCount > 0 ? (int) failedRecordsCount
                : (totalRecords > 0 ? totalRecords : errors.size());

        AccountCatalogueImportResponse response = AccountCatalogueImportResponse.builder()
                .entId(entId)
                .fileName(fileName)
                .status(ImportStatus.FAILED)
                .totalRecords(totalRecords)
                .successfulImports(0)
                .failedImports(failedImports)
                .duplicatesSkipped(0)
                .errors(errors)
                .build();

        return response;
    }

    /**
     * Determina el estado de la importación basado en los resultados.
     * 
     * Casos:
     * 1. Solo éxitos → COMPLETED
     * 2. Éxitos + fallos → COMPLETED_WITH_ERRORS
     * 3. Solo fallos → FAILED
     * 4. Solo duplicados (sin éxitos ni fallos) → COMPLETED
     * 5. Sin datos → FAILED
     */
    private ImportStatus determineImportStatus(int successCount, int failureCount, int duplicatesSkipped) {
        if (failureCount == 0 && successCount > 0) {
            // Caso 1: Solo registros exitosos (puede tener duplicados también)
            return ImportStatus.COMPLETED;
        } else if (successCount > 0 && failureCount > 0) {
            // Caso 2: Mezcla de éxitos y fallos
            return ImportStatus.COMPLETED_WITH_ERRORS;
        } else if (successCount == 0 && failureCount > 0) {
            // Caso 3: Solo fallos
            return ImportStatus.FAILED;
        } else if (successCount == 0 && failureCount == 0 && duplicatesSkipped > 0) {
            // Caso 4: Solo duplicados (importación exitosa sin nuevos registros)
            return ImportStatus.COMPLETED;
        } else {
            // Caso 5: Sin datos procesados
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
                        .errorType(ImportErrorType.VALIDATION_ERROR)
                        .build()))
                .build();
    }
}
