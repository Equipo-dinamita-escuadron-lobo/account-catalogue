package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @brief Servicio para construcción de respuestas de importación
 *
 * Construye respuestas estructuradas para operaciones de importación,
 * incluyendo estadísticas de éxito, errores y métricas de procesamiento.
 */
@Slf4j
@Service
public class AccountCatalogueImportResponseBuilder {

    /**
     * @brief Construye respuesta de importación con métricas calculadas y estado determinado
     *
     * Calcula métricas de importación: cuentas filas únicas con errores, determina estado
     * basado en combinación de éxitos/fallos/duplicados, y construye respuesta estructurada
     * con logging detallado de resultados.
     * @param entId ID de empresa
     * @param fileName nombre del archivo procesado
     * @param totalRecords total de registros leídos del archivo
     * @param successCount cantidad de registros insertados exitosamente
     * @param failureCount cantidad de registros que fallaron
     * @param duplicatesSkipped cantidad de duplicados omitidos
     * @param errors lista completa de errores encontrados
     * @return respuesta estructurada con métricas y estado de importación
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

    /**
     * @brief Construye respuesta de importación fallida con conteo de errores únicos
     * @param entId ID de empresa
     * @param fileName nombre del archivo procesado
     * @param totalRecords total de registros analizados
     * @param errors lista completa de errores encontrados
     * @return respuesta con estado FAILED y métricas calculadas
     */
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
     * @brief Determina estado de importación basado en lógica de negocio específica
     * @param successCount cantidad de registros exitosos
     * @param failureCount cantidad de registros fallidos
     * @param duplicatesSkipped cantidad de duplicados omitidos
     * @return estado de importación
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
     * @brief Crea respuesta estandarizada para archivos Excel sin contenido de datos
     * @param entId ID de empresa
     * @param fileName nombre del archivo vacío
     * @return respuesta con estado FAILED y error de archivo vacío
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
