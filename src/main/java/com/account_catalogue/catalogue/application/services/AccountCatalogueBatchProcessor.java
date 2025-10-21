package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.AccountCodeUtils;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicio especializado en procesamiento por lotes de cuentas contables.
 * Procesa registros en lotes transaccionales para optimizar rendimiento.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueBatchProcessor {

    private final AccountCatalogueCreateService createService;
    private final AccountCatalogueDataConverter dataConverter;
    private final AccountCatalogueHierarchyProcessor hierarchyProcessor;

    /**
     * Procesa un lote de cuentas en transacciones.
     * Cada lote se procesa en una transacción independiente.
     * 
     * @param accountsData lista ordenada de cuentas a procesar
     * @param entId identificador de la empresa
     * @return resultado del procesamiento con estadísticas
     */
    public BatchProcessingResult processBatch(List<AccountCatalogueExcelData> accountsData, String entId) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        int skippedCount = 0;

        // Dividir en lotes de tamaño BATCH_SIZE
        List<List<AccountCatalogueExcelData>> batches = partitionList(accountsData, ImportConstants.Defaults.BATCH_SIZE);

        // Mapa para almacenar cuentas ya procesadas y poder usarlas como padres
        Map<String, AccountCatalogue> processedAccountsMap = new HashMap<>();

        // Procesar cada lote
        for (int batchIndex = 0; batchIndex < batches.size(); batchIndex++) {
            List<AccountCatalogueExcelData> batch = batches.get(batchIndex);
            
            try {
                BatchResult batchResult = processSingleBatch(batch, entId, processedAccountsMap, batchIndex + 1);
                
                successCount += batchResult.getSuccessCount();
                failureCount += batchResult.getFailureCount();
                skippedCount += batchResult.getSkippedCount();
                errors.addAll(batchResult.getErrors());

            } catch (Exception e) {
                
                // Registrar error para todos los registros del lote
                for (AccountCatalogueExcelData record : batch) {
                    errors.add(ImportErrorDetail.builder()
                            .rowNumber(record.getRowNumber())
                            .errorCode(ImportConstants.ErrorCodes.SYSTEM_ERROR)
                            .errorMessage("Error del sistema procesando lote: " + e.getMessage())
                            .errorType(ImportErrorType.SYSTEM_ERROR)
                            .build());
                }
                
                failureCount += batch.size();

                // Si CONTINUE_ON_ERROR es false, detener
                if (!ImportConstants.Defaults.CONTINUE_ON_ERROR) {
                    
                    // Marcar registros restantes como omitidos
                    for (int i = batchIndex + 1; i < batches.size(); i++) {
                        skippedCount += batches.get(i).size();
                    }
                    
                    break;
                }
            }
        }

        return BatchProcessingResult.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .skippedCount(skippedCount)
                .errors(errors)
                .totalProcessed(successCount + failureCount)
                .build();
    }

    /**
     * Procesa un lote individual en una transacción.
     */
    @Transactional
    protected BatchResult processSingleBatch(List<AccountCatalogueExcelData> batch, String entId, 
                                            Map<String, AccountCatalogue> processedAccountsMap, int batchNumber) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // Obtener códigos únicos de padres necesarios para este lote
        Set<String> requiredParentCodes = extractRequiredParentCodes(batch);
        
        // Construir mapa de padres desde BD (solo los que no están en processedAccountsMap)
        Set<String> missingParentCodes = new HashSet<>(requiredParentCodes);
        missingParentCodes.removeAll(processedAccountsMap.keySet());
        
        Map<String, AccountCatalogueEntity> parentsMap = 
                hierarchyProcessor.buildParentMapFromDatabase(missingParentCodes, entId);

        // Procesar cada registro del lote
        for (AccountCatalogueExcelData excelData : batch) {
            try {
                // Convertir a dominio
                AccountCatalogue accountCatalogue = dataConverter.convertToAccountCatalogue(
                        excelData, parentsMap, processedAccountsMap);

                // Crear usando el servicio existente (ya tiene validaciones)
                AccountCatalogue created = createService.createAccountCatalogue(accountCatalogue);

                // Almacenar en mapa de procesados para usar como padre en siguientes registros
                processedAccountsMap.put(created.getCode(), created);

                successCount++;
         
            } catch (Exception e) {
                failureCount++;
                
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(excelData.getRowNumber())
                        .columnName(ImportConstants.CODE_COLUMN)
                        .fieldValue(excelData.getCode())
                        .errorCode(ImportConstants.ErrorCodes.SYSTEM_ERROR)
                        .errorMessage("Error creando cuenta: " + e.getMessage())
                        .errorType(ImportErrorType.SYSTEM_ERROR)
                        .build());

                // Si CONTINUE_ON_ERROR es false, propagar excepción para rollback
                if (!ImportConstants.Defaults.CONTINUE_ON_ERROR) {
                    throw e;
                }
            }
        }

        return BatchResult.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .skippedCount(0)
                .errors(errors)
                .build();
    }

    /**
     * Extrae los códigos de padres necesarios para un lote.
     */
    private Set<String> extractRequiredParentCodes(List<AccountCatalogueExcelData> batch) {
        Set<String> parentCodes = new HashSet<>();
        
        for (AccountCatalogueExcelData data : batch) {
            String parentCode = AccountCodeUtils.extractParentCode(data.getCode());
            if (parentCode != null) {
                parentCodes.add(parentCode);
            }
        }
        
        return parentCodes;
    }

    /**
     * Divide una lista en sublistas del tamaño especificado.
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(new ArrayList<>(list.subList(i, end)));
        }
        
        return batches;
    }

    /**
     * Clase interna para resultado de procesamiento de un lote.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BatchResult {
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Clase que representa el resultado de procesamiento completo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchProcessingResult {
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private List<ImportErrorDetail> errors;
        private int totalProcessed;
    }
}

