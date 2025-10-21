package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueImportInputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio orquestador principal para importación de catálogo de cuentas desde Excel.
 * Implementa el patrón Pipeline para procesar la importación en 6 fases:
 * 1. Validación de archivo
 * 2. Parseo de Excel
 * 3. Validación de datos
 * 4. Detección de duplicados
 * 5. Ordenamiento y validación jerárquica
 * 6. Procesamiento en lotes
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
     * Importa catálogo de cuentas desde un archivo Excel.
     * Orquesta todo el proceso de importación en fases.
     */
    @Override
    public AccountCatalogueImportResponse importAccountCatalogueFromExcel(AccountCatalogueImportRequest request) {
        String entId = request.getEntId();
        String fileName = request.getExcelFile().getOriginalFilename();
        List<ImportErrorDetail> allErrors = new ArrayList<>();

        log.info("=== Iniciando importación de catálogo de cuentas ===");
        log.info("Empresa: {}, Archivo: {}", entId, fileName);

        try {
            // ===== FASE 1: VALIDACIÓN DE ARCHIVO =====
            log.info("Fase 1: Validando archivo...");
            fileValidationService.validate(request.getExcelFile());
            log.info("✓ Archivo válido");

            // ===== FASE 2: PARSEO DE EXCEL =====
            log.info("Fase 2: Parseando archivo Excel...");
            AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult = 
                    excelParsingService.parseExcelFile(request.getExcelFile(), entId);
            
            allErrors.addAll(parsingResult.getErrors());
            
            if (parsingResult.getAccountsData().isEmpty()) {
                log.warn("✗ No se encontraron datos válidos en el archivo");
                return responseBuilder.buildEmptyFileResponse(entId, fileName);
            }
            
            log.info("✓ Parseados {} registros con {} errores de formato", 
                    parsingResult.getAccountsData().size(), parsingResult.getErrors().size());

            // Si hay errores de parseo críticos y CONTINUE_ON_ERROR es false, detener
            if (!parsingResult.getErrors().isEmpty() && 
                !com.account_catalogue.catalogue.domain.utils.ImportConstants.Defaults.CONTINUE_ON_ERROR) {
                log.error("✗ Deteniendo importación por errores de parseo (CONTINUE_ON_ERROR=false)");
                return responseBuilder.buildFailedResponse(entId, fileName, 
                        parsingResult.getTotalRows(), allErrors);
            }

            // ===== FASE 3: VALIDACIÓN DE DATOS =====
            log.info("Fase 3: Validando datos...");
            AccountCatalogueBatchValidationService.BatchValidationResult validationResult = 
                    batchValidationService.validateBatch(
                            parsingResult.getAccountsData(), entId, parsingResult.getColumnMap());
            
            allErrors.addAll(validationResult.getErrors());
            
            log.info("✓ Validados {} registros: {} válidos, {} con errores", 
                    validationResult.getTotalProcessed(), 
                    validationResult.getValidCount(), 
                    validationResult.getErrorCount());

            if (validationResult.getValidRecords().isEmpty()) {
                log.warn("✗ No hay registros válidos para importar después de validaciones");
                return responseBuilder.buildFailedResponse(entId, fileName, 
                        parsingResult.getTotalRows(), allErrors);
            }

            // Si hay errores de validación y CONTINUE_ON_ERROR es false, detener
            if (!validationResult.getErrors().isEmpty() && 
                !com.account_catalogue.catalogue.domain.utils.ImportConstants.Defaults.CONTINUE_ON_ERROR) {
                log.error("✗ Deteniendo importación por errores de validación (CONTINUE_ON_ERROR=false)");
                return responseBuilder.buildFailedResponse(entId, fileName, 
                        parsingResult.getTotalRows(), allErrors);
            }

            // ===== FASE 4: DETECCIÓN DE DUPLICADOS =====
            log.info("Fase 4: Detectando duplicados...");
            AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult = 
                    duplicateDetectionService.detectDuplicates(validationResult.getValidRecords(), entId);
            
            allErrors.addAll(duplicateResult.getErrors());
            
            log.info("✓ Analizados {} registros: {} únicos, {} duplicados omitidos", 
                    duplicateResult.getTotalAnalyzed(), 
                    duplicateResult.getUniqueCount(), 
                    duplicateResult.getDuplicateCount());

            if (duplicateResult.getUniqueRecords().isEmpty()) {
                log.warn("✗ No hay registros únicos para importar después de eliminar duplicados");
                return responseBuilder.buildFailedResponse(entId, fileName, 
                        parsingResult.getTotalRows(), allErrors);
            }

            // ===== FASE 5: ORDENAMIENTO Y VALIDACIÓN JERÁRQUICA =====
            log.info("Fase 5: Validando y ordenando jerarquía...");
            List<AccountCatalogueExcelData> sortedAccounts = 
                    hierarchyProcessor.sortAndValidateHierarchy(duplicateResult.getUniqueRecords(), entId);
            
            log.info("✓ Jerarquía validada y ordenada: {} cuentas listas para procesar", sortedAccounts.size());

            // ===== FASE 6: PROCESAMIENTO EN LOTES =====
            log.info("Fase 6: Procesando cuentas en lotes...");
            AccountCatalogueBatchProcessor.BatchProcessingResult processingResult = 
                    batchProcessor.processBatch(sortedAccounts, entId);
            
            allErrors.addAll(processingResult.getErrors());
            
            log.info("✓ Procesamiento completado: {} exitosos, {} fallidos, {} omitidos", 
                    processingResult.getSuccessCount(), 
                    processingResult.getFailureCount(), 
                    processingResult.getSkippedCount());

            // ===== CONSTRUIR RESPUESTA FINAL =====
            AccountCatalogueImportResponse response = responseBuilder.buildSuccessResponse(
                    entId,
                    fileName,
                    parsingResult.getTotalRows(),
                    processingResult.getSuccessCount(),
                    processingResult.getFailureCount(),
                    duplicateResult.getDuplicateCount(),
                    allErrors
            );

            log.info("=== Importación finalizada: {} ===", response.getStatus());
            return response;

        } catch (Exception e) {
            log.error("✗ Error inesperado durante la importación: {}", e.getMessage(), e);
            
            allErrors.add(ImportErrorDetail.builder()
                    .errorCode("SYSTEM_ERROR")
                    .errorMessage("Error del sistema: " + e.getMessage())
                    .errorType(com.account_catalogue.catalogue.domain.enums.ImportErrorType.SYSTEM_ERROR)
                    .build());

            return responseBuilder.buildFailedResponse(entId, fileName, 0, allErrors);
        }
    }
}

