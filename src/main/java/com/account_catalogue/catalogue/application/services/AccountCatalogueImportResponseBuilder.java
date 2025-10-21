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
                        .errorType(ImportErrorType.VALIDATION_ERROR)
                        .build()))
                .build();
    }
}
