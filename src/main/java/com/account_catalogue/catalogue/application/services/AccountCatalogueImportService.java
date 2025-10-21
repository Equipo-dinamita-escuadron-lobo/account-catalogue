package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueImportInputPort;
import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueImportService implements IAccountCatalogueImportInputPort {

        private final AccountCatalogueFileValidationService fileValidationService;
        private final AccountCatalogueExcelParsingService excelParsingService;
        private final AccountCatalogueBatchValidationService batchValidationService;
        private final AccountCatalogueDuplicateDetectionService duplicateDetectionService;
        private final AccountCatalogueHierarchyProcessor hierarchyProcessor;
        private final AccountCatalogueBatchProcessor batchProcessor;
        private final AccountCatalogueImportResponseBuilder responseBuilder;

        /**
         * Importa catálogo de cuentas desde un archivo Excel.
         * Orquesta todo el proceso de importación en fases.
         */
        @Override
        public AccountCatalogueImportResponse importAccountCatalogueFromExcel(AccountCatalogueImportRequest request) {
                String entId = request.getEntId();
                String fileName = request.getExcelFile().getOriginalFilename();
                List<ImportErrorDetail> allErrors = new ArrayList<>();

                try {
                        fileValidationService.validate(request.getExcelFile());

                        AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult = excelParsingService
                                        .parseExcelFile(request.getExcelFile(), entId);

                        allErrors.addAll(parsingResult.getErrors());

                        if (parsingResult.getAccountsData().isEmpty()) {
                                return responseBuilder.buildEmptyFileResponse(entId, fileName);
                        }

                        AccountCatalogueBatchValidationService.BatchValidationResult validationResult = batchValidationService
                                        .validateBatch(
                                                        parsingResult.getAccountsData(), entId,
                                                        parsingResult.getColumnMap());

                        allErrors.addAll(validationResult.getErrors());

                        if (validationResult.getValidRecords().isEmpty()) {
                                return responseBuilder.buildFailedResponse(entId, fileName,
                                                parsingResult.getTotalRows(), allErrors);
                        }

                        AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult = duplicateDetectionService
                                        .detectDuplicates(validationResult.getValidRecords(), entId);

                        allErrors.addAll(duplicateResult.getErrors());

                        // Si no hay registros únicos
                        if (duplicateResult.getUniqueRecords().isEmpty()) {
                                if (duplicateResult.getDuplicateCount() > 0) {
                                        // Hay duplicados (con o sin errores de validación)
                                        if (allErrors.isEmpty()) {
                                                // Todos son duplicados, sin errores
                                                return responseBuilder.buildSuccessResponse(
                                                                entId,
                                                                fileName,
                                                                parsingResult.getTotalRows(),
                                                                0, // successCount
                                                                0, // failureCount
                                                                duplicateResult.getDuplicateCount(),
                                                                allErrors);
                                        } else {
                                                // Hay duplicados Y errores de validación
                                                // Reportar duplicados + errores
                                                long failedRecords = allErrors.stream()
                                                                .map(ImportErrorDetail::getRowNumber)
                                                                .filter(rowNum -> rowNum != null)
                                                                .distinct()
                                                                .count();

                                                return responseBuilder.buildSuccessResponse(
                                                                entId,
                                                                fileName,
                                                                parsingResult.getTotalRows(),
                                                                0, // successCount
                                                                (int) failedRecords, // failureCount
                                                                duplicateResult.getDuplicateCount(),
                                                                allErrors);
                                        }
                                }
                                // No hay duplicados ni registros únicos, entonces falló
                                return responseBuilder.buildFailedResponse(entId, fileName,
                                                parsingResult.getTotalRows(), allErrors);
                        }

                        List<AccountCatalogueExcelData> sortedAccounts = hierarchyProcessor
                                        .sortByHierarchy(duplicateResult.getUniqueRecords());

                        List<ImportErrorDetail> hierarchyErrors = hierarchyProcessor
                                        .validateHierarchyWithDetails(sortedAccounts, entId);

                        allErrors.addAll(hierarchyErrors);

                        if (!hierarchyErrors.isEmpty()) {

                                if (!ImportConstants.Defaults.CONTINUE_ON_ERROR) {
                                        return responseBuilder.buildFailedResponse(entId, fileName,
                                                        parsingResult.getTotalRows(), allErrors);
                                }

                                Set<Integer> errorRows = hierarchyErrors.stream()
                                                .map(ImportErrorDetail::getRowNumber)
                                                .collect(Collectors.toSet());

                                sortedAccounts = sortedAccounts.stream()
                                                .filter(account -> !errorRows.contains(account.getRowNumber()))
                                                .collect(Collectors.toList());
                        }

                        // Si no quedan cuentas después de filtrar errores de jerarquía
                        if (sortedAccounts.isEmpty()) {
                                if (duplicateResult.getDuplicateCount() > 0) {
                                        // Hay duplicados (con o sin otros errores)
                                        long failedRecords = allErrors.stream()
                                                        .map(ImportErrorDetail::getRowNumber)
                                                        .filter(rowNum -> rowNum != null)
                                                        .distinct()
                                                        .count();

                                        return responseBuilder.buildSuccessResponse(
                                                        entId,
                                                        fileName,
                                                        parsingResult.getTotalRows(),
                                                        0, // successCount
                                                        (int) failedRecords, // failureCount
                                                        duplicateResult.getDuplicateCount(),
                                                        allErrors);
                                }
                                // No hay duplicados ni registros válidos
                                return responseBuilder.buildFailedResponse(entId, fileName,
                                                parsingResult.getTotalRows(), allErrors);
                        }

                        AccountCatalogueBatchProcessor.BatchProcessingResult processingResult = batchProcessor
                                        .processBatch(sortedAccounts, entId);

                        allErrors.addAll(processingResult.getErrors());

                        AccountCatalogueImportResponse response = responseBuilder.buildSuccessResponse(
                                        entId,
                                        fileName,
                                        parsingResult.getTotalRows(),
                                        processingResult.getSuccessCount(),
                                        processingResult.getFailureCount(),
                                        duplicateResult.getDuplicateCount(),
                                        allErrors);

                        return response;

                } catch (Exception e) {
                        allErrors.add(ImportErrorDetail.builder()
                                        .errorCode("SYSTEM_ERROR")
                                        .errorMessage("Error del sistema: " + e.getMessage())
                                        .errorType(ImportErrorType.SYSTEM_ERROR)
                                        .build());

                        return responseBuilder.buildFailedResponse(entId, fileName, 0, allErrors);
                }
        }
}
