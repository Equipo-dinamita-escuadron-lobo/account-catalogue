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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @brief Servicio para importación masiva de cuentas contables desde Excel
 *
 *        Coordina el proceso completo de importación de cuentas desde archivos
 *        Excel,
 *        incluyendo validación, procesamiento por lotes y manejo de errores.
 */
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
         * @brief Maneja lógica de duplicados sin registros únicos
         * @param entId ID de empresa
         * @param fileName nombre del archivo
         * @param parsingResult resultado del parsing del archivo
         * @param duplicateResult resultado de la detección de duplicados
         * @param allErrors errores de validación
         * @return respuesta de importación
         */
        private AccountCatalogueImportResponse handleNoUniqueRecords(String entId, String fileName,
                        AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult,
                        AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult,
                        List<ImportErrorDetail> allErrors) {

                if (duplicateResult.getDuplicateCount() > 0) {
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
                                long failedRecords = allErrors.stream()
                                                .map(ImportErrorDetail::getRowNumber)
                                                .filter(Objects::nonNull)
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

        /**
         * @brief Maneja caso sin cuentas válidas después de filtro jerárquico
         * @param entId ID de empresa
         * @param fileName nombre del archivo
         * @param parsingResult resultado del parsing del archivo
         * @param duplicateResult resultado de la detección de duplicados
         * @param allErrors errores de validación
         * @return respuesta de importación
         */
        private AccountCatalogueImportResponse handleEmptyAccountsAfterHierarchyFilter(String entId, String fileName,
                        AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult,
                        AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult,
                        List<ImportErrorDetail> allErrors) {

                if (duplicateResult.getDuplicateCount() > 0) {
                        // Hay duplicados (con o sin otros errores)
                        long failedRecords = allErrors.stream()
                                        .map(ImportErrorDetail::getRowNumber)
                                        .filter(Objects::nonNull)
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

        /**
         * @brief Coordina proceso completo de importación desde Excel
         * @param request solicitud con archivo Excel y configuración
         * @return respuesta detallada con resultados de importación
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
                                return handleNoUniqueRecords(entId, fileName, parsingResult, duplicateResult,
                                                allErrors);
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
                                                .toList();
                        }

                        // Si no quedan cuentas después de filtrar errores de jerarquía
                        if (sortedAccounts.isEmpty()) {
                                return handleEmptyAccountsAfterHierarchyFilter(entId, fileName, parsingResult,
                                                duplicateResult, allErrors);
                        }

                        AccountCatalogueBatchProcessor.BatchProcessingResult processingResult = batchProcessor
                                        .processBatch(sortedAccounts, entId);

                        allErrors.addAll(processingResult.getErrors());

                        return responseBuilder.buildSuccessResponse(
                                        entId,
                                        fileName,
                                        parsingResult.getTotalRows(),
                                        processingResult.getSuccessCount(),
                                        processingResult.getFailureCount(),
                                        duplicateResult.getDuplicateCount(),
                                        allErrors);

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
