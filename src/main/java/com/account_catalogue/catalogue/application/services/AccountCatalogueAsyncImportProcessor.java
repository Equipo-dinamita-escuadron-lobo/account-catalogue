package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueImportRequest;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueImportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @brief Procesador asíncrono para importación de catálogo de cuentas desde Excel
 *
 * Maneja el flujo completo de importación en un hilo separado, actualizando
 * el estado del trabajo en tiempo real a través del JobTracker.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueAsyncImportProcessor {

    private final AccountCatalogueImportJobTracker jobTracker;
    private final AccountCatalogueExcelParsingService excelParsingService;
    private final AccountCatalogueBatchValidationService batchValidationService;
    private final AccountCatalogueDuplicateDetectionService duplicateDetectionService;
    private final AccountCatalogueHierarchyProcessor hierarchyProcessor;
    private final AccountCatalogueBatchProcessor batchProcessor;

    /**
     * @brief Procesa la importación de forma asíncrona
     * @param request solicitud de importación con archivo y configuración
     * @param jobId identificador del trabajo
     * @param fileBytes contenido del archivo Excel en bytes
     */
    @Async
    public void processImportAsync(AccountCatalogueImportRequest request, String jobId, byte[] fileBytes) {
        String entId = request.getEntId();
        String fileName = request.getFileName();
        
        log.info("JobId {}: Iniciando importación asíncrona de catálogo de cuentas para entidad: {}, archivo: {}", 
                jobId, entId, fileName);

        long startTime = System.currentTimeMillis();
        List<ImportErrorDetail> allErrors = new ArrayList<>();
        
        // Tracking de tiempos por fase
        Map<String, Long> phaseTimes = new LinkedHashMap<>();

        try {
            jobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
            jobTracker.updateProgress(jobId, 5);

            // ==================== FASE 1: PARSING ====================
            long phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 1 - Parsing de archivo Excel", jobId);
            
            AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult = 
                    excelParsingService.parseExcelFileFromBytes(fileBytes, entId);
            
            allErrors.addAll(parsingResult.getErrors());
            long phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("1. Parsing Excel", phaseDuration);
            log.info("JobId {}: FASE 1 completada en {} ms. Registros parseados: {}, Errores: {}", 
                    jobId, phaseDuration, parsingResult.getAccountsData().size(), parsingResult.getErrors().size());

            if (parsingResult.getAccountsData().isEmpty()) {
                handleEmptyFile(jobId, entId, fileName, allErrors);
                return;
            }

            jobTracker.updateProgress(jobId, 20);
            jobTracker.updateJobMetrics(jobId, parsingResult.getTotalRows(), 0, 0, 0);

            // ==================== FASE 2: VALIDACIÓN ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 2 - Validación de datos", jobId);
            
            AccountCatalogueBatchValidationService.BatchValidationResult validationResult = 
                    batchValidationService.validateBatch(
                            parsingResult.getAccountsData(), entId, parsingResult.getColumnMap());
            
            allErrors.addAll(validationResult.getErrors());
            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("2. Validación", phaseDuration);
            log.info("JobId {}: FASE 2 completada en {} ms. Registros válidos: {}, Errores: {}", 
                    jobId, phaseDuration, validationResult.getValidRecords().size(), 
                    validationResult.getErrors().size());

            if (validationResult.getValidRecords().isEmpty()) {
                handleAllValidationsFailed(jobId, entId, fileName, parsingResult.getTotalRows(), allErrors);
                return;
            }

            jobTracker.updateProgress(jobId, 40);

            // ==================== FASE 3: DETECCIÓN DE DUPLICADOS ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 3 - Detección de duplicados", jobId);
            
            AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult = 
                    duplicateDetectionService.detectDuplicates(validationResult.getValidRecords(), entId);
            
            allErrors.addAll(duplicateResult.getErrors());
            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("3. Detección Duplicados", phaseDuration);
            log.info("JobId {}: FASE 3 completada en {} ms. Únicos: {}, Duplicados: {}", 
                    jobId, phaseDuration, duplicateResult.getUniqueRecords().size(), 
                    duplicateResult.getDuplicateCount());

            if (duplicateResult.getUniqueRecords().isEmpty()) {
                handleNoDuplicates(jobId, entId, fileName, parsingResult, duplicateResult, allErrors);
                return;
            }

            jobTracker.updateProgress(jobId, 60);

            // ==================== FASE 4: ORDENAMIENTO JERÁRQUICO ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 4 - Ordenamiento jerárquico", jobId);
            
            List<AccountCatalogueExcelData> sortedAccounts = 
                    hierarchyProcessor.sortByHierarchy(duplicateResult.getUniqueRecords());
            
            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("4. Ordenamiento", phaseDuration);
            log.info("JobId {}: FASE 4 completada en {} ms. Cuentas ordenadas: {}", 
                    jobId, phaseDuration, sortedAccounts.size());

            jobTracker.updateProgress(jobId, 70);

            // ==================== FASE 5: VALIDACIÓN DE JERARQUÍA ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 5 - Validación de jerarquía", jobId);
            
            List<ImportErrorDetail> hierarchyErrors = 
                    hierarchyProcessor.validateHierarchyWithDetails(sortedAccounts, entId);
            
            allErrors.addAll(hierarchyErrors);
            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("5. Validación Jerarquía", phaseDuration);
            log.info("JobId {}: FASE 5 completada en {} ms. Errores de jerarquía: {}", 
                    jobId, phaseDuration, hierarchyErrors.size());

            if (!hierarchyErrors.isEmpty()) {
                Set<Integer> errorRows = hierarchyErrors.stream()
                        .map(ImportErrorDetail::getRowNumber)
                        .collect(Collectors.toSet());

                sortedAccounts = sortedAccounts.stream()
                        .filter(account -> !errorRows.contains(account.getRowNumber()))
                        .toList();
            }

            if (sortedAccounts.isEmpty()) {
                handleEmptyAfterHierarchy(jobId, entId, fileName, parsingResult, duplicateResult, allErrors);
                return;
            }

            jobTracker.updateProgress(jobId, 80);

            // ==================== FASE 6: PROCESAMIENTO BATCH ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 6 - Procesamiento batch de {} cuentas", jobId, sortedAccounts.size());
            
            AccountCatalogueBatchProcessor.BatchProcessingResult processingResult = 
                    batchProcessor.processBatch(sortedAccounts, entId);
            
            allErrors.addAll(processingResult.getErrors());
            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("6. Procesamiento Batch", phaseDuration);
            log.info("JobId {}: FASE 6 completada en {} ms. Exitosos: {}, Fallidos: {}", 
                    jobId, phaseDuration, processingResult.getSuccessCount(), 
                    processingResult.getFailureCount());

            // ==================== FINALIZACIÓN ====================
            long totalDuration = System.currentTimeMillis() - startTime;
            
            jobTracker.updateJobMetrics(
                    jobId,
                    parsingResult.getTotalRows(),
                    processingResult.getSuccessCount(),
                    processingResult.getFailureCount(),
                    duplicateResult.getDuplicateCount()
            );
            jobTracker.addErrors(jobId, allErrors);
            jobTracker.updateProgress(jobId, 100);

            ImportStatus finalStatus = determineFinalStatus(
                    processingResult.getSuccessCount(), 
                    processingResult.getFailureCount(),
                    allErrors.size()
            );
            jobTracker.updateJobStatus(jobId, finalStatus);

            printPhaseTimesTable(jobId, totalDuration, phaseTimes, processingResult, duplicateResult, parsingResult);
            
            log.info("JobId {}: Importación completada exitosamente en {} ms", jobId, totalDuration);

        } catch (AccountCatalogueImportException e) {
            handleCriticalError(jobId, entId, fileName, e.getMessage(), allErrors);
        } catch (Exception e) {
            log.error("JobId {}: Error crítico durante la importación", jobId, e);
            handleCriticalError(jobId, entId, fileName, "Error del sistema: " + e.getMessage(), allErrors);
        }
    }

    private void handleEmptyFile(String jobId, String entId, String fileName, 
                                  List<ImportErrorDetail> allErrors) {
        log.warn("JobId {}: Archivo vacío o sin datos válidos", jobId);
        jobTracker.updateJobMetrics(jobId, 0, 0, 0, 0);
        jobTracker.addErrors(jobId, allErrors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private void handleAllValidationsFailed(String jobId, String entId, String fileName, 
                                            int totalRows, List<ImportErrorDetail> allErrors) {
        log.warn("JobId {}: Todas las validaciones fallaron", jobId);
        long failedRecords = allErrors.stream()
                .map(ImportErrorDetail::getRowNumber)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        
        jobTracker.updateJobMetrics(jobId, totalRows, 0, (int) failedRecords, 0);
        jobTracker.addErrors(jobId, allErrors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private void handleNoDuplicates(String jobId, String entId, String fileName,
                                    AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult,
                                    AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult,
                                    List<ImportErrorDetail> allErrors) {
        log.warn("JobId {}: No hay registros únicos después de la detección de duplicados", jobId);
        
        long failedRecords = allErrors.stream()
                .map(ImportErrorDetail::getRowNumber)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        jobTracker.updateJobMetrics(
                jobId,
                parsingResult.getTotalRows(),
                0,
                (int) failedRecords,
                duplicateResult.getDuplicateCount()
        );
        jobTracker.addErrors(jobId, allErrors);
        
        ImportStatus status = duplicateResult.getDuplicateCount() > 0 && allErrors.isEmpty()
                ? ImportStatus.COMPLETED
                : ImportStatus.COMPLETED_WITH_ERRORS;
        
        jobTracker.updateJobStatus(jobId, status);
        jobTracker.updateProgress(jobId, 100);
    }

    private void handleEmptyAfterHierarchy(String jobId, String entId, String fileName,
                                           AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult,
                                           AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult,
                                           List<ImportErrorDetail> allErrors) {
        log.warn("JobId {}: No quedan cuentas válidas después de la validación de jerarquía", jobId);
        
        long failedRecords = allErrors.stream()
                .map(ImportErrorDetail::getRowNumber)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        jobTracker.updateJobMetrics(
                jobId,
                parsingResult.getTotalRows(),
                0,
                (int) failedRecords,
                duplicateResult.getDuplicateCount()
        );
        jobTracker.addErrors(jobId, allErrors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private void handleCriticalError(String jobId, String entId, String fileName, 
                                     String errorMessage, List<ImportErrorDetail> allErrors) {
        log.error("JobId {}: Error crítico: {}", jobId, errorMessage);
        
        jobTracker.updateJobMetrics(jobId, 0, 0, 0, 0);
        jobTracker.addErrors(jobId, allErrors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private ImportStatus determineFinalStatus(int successCount, int failureCount, int errorCount) {
        if (successCount > 0 && (failureCount > 0 || errorCount > 0)) {
            return ImportStatus.COMPLETED_WITH_ERRORS;
        } else if (successCount > 0) {
            return ImportStatus.COMPLETED;
        } else {
            return ImportStatus.FAILED;
        }
    }

    private void printPhaseTimesTable(String jobId, long totalDuration, Map<String, Long> phaseTimes,
                                      AccountCatalogueBatchProcessor.BatchProcessingResult processingResult,
                                      AccountCatalogueDuplicateDetectionService.DuplicateDetectionResult duplicateResult,
                                      AccountCatalogueExcelParsingService.ExcelParsingResult parsingResult) {
        
        double totalSeconds = totalDuration / 1000.0;
        double performance = parsingResult.getTotalRows() * 1000.0 / totalDuration;
        
        log.info("╔══════════════════════════════════════════════════════════════════════════════╗");
        log.info("║  RESUMEN DE TIEMPOS - JobId: {}                                        ║", jobId.substring(0, 8));
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        log.info("║  Fase                          │ Tiempo (ms) │ Tiempo (s) │      %          ║");
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        
        for (Map.Entry<String, Long> entry : phaseTimes.entrySet()) {
            String fase = entry.getKey();
            long tiempo = entry.getValue();
            double segundos = tiempo / 1000.0;
            double porcentaje = (tiempo * 100.0) / totalDuration;
            
            // Construir la línea completa con String.format primero
            String linea = String.format("║  %-29s │ %,11d │ %10.2f │ %6.1f%%         ║",
                    fase, tiempo, segundos, porcentaje);
            log.info(linea);
        }
        
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        String lineaTotal = String.format("║  TOTAL                         │ %,11d │ %10.2f │  100.0%%         ║",
                totalDuration, totalSeconds);
        log.info(lineaTotal);
        
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        String lineaRegistros = String.format("║  Total Registros: %,8d                                                     ║",
                parsingResult.getTotalRows());
        log.info(lineaRegistros);
        
        String lineaRendimiento = String.format("║  Rendimiento: %8.2f registros/seg                                        ║",
                performance);
        log.info(lineaRendimiento);
        log.info("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        log.info("╔══════════════════════════════════════════════════════════════════════════════╗");
        log.info("║  RESUMEN DE IMPORTACIÓN - JobId: {}                                    ║", jobId.substring(0, 8));
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        
        String lineaExitosas = String.format("║  Importaciones Exitosas:     %,8d                                          ║",
                processingResult.getSuccessCount());
        log.info(lineaExitosas);
        
        String lineaFallidas = String.format("║  Importaciones Fallidas:     %,8d                                          ║",
                processingResult.getFailureCount());
        log.info(lineaFallidas);
        
        String lineaDuplicados = String.format("║  Duplicados Omitidos:        %,8d                                          ║",
                duplicateResult.getDuplicateCount());
        log.info(lineaDuplicados);
        
        log.info("╚══════════════════════════════════════════════════════════════════════════════╝");
    }
}

