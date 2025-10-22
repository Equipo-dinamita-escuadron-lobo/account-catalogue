package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueErrorCode;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueImportException;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.StringNormalizer;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueDuplicateDetectionService {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    /**
     * Procesa un registro individual para determinar si es duplicado.
     */
    private boolean isDuplicateRecord(AccountCatalogueExcelData accountData, String entId,
            Set<String> seenCodes, Set<String> seenDescriptions,
            Map<String, AccountCatalogueEntity> dbDuplicatesByCode,
            Map<String, AccountCatalogueEntity> dbDuplicatesByDescription) {

        String code = accountData.getCode();
        String description = accountData.getDescription();
        String normalizedDescription = StringNormalizer.normalizeForComparison(description);
        normalizedDescription = normalizedDescription != null ? normalizedDescription : "";
        String codeKey = code + "_" + entId;
        String descKey = normalizedDescription + "_" + entId;

        // Verificar duplicados internos
        if (seenCodes.contains(codeKey)) {
            return true;
        }
        seenCodes.add(codeKey);

        if (seenDescriptions.contains(descKey)) {
            return true;
        }
        seenDescriptions.add(descKey);

        // Verificar duplicados en BD
        return dbDuplicatesByCode.containsKey(code) ||
               dbDuplicatesByDescription.containsKey(normalizedDescription);
    }

    /**
     * Detecta duplicados internos en el Excel y contra la base de datos.
     * Los duplicados se detectan por código Y descripción (case-insensitive sin
     * acentos).
     * Los duplicados se omiten silenciosamente sin generar errores.
     */
    public DuplicateDetectionResult detectDuplicates(List<AccountCatalogueExcelData> accountsData, String entId) {
        Set<String> seenCodes = new HashSet<>();
        Set<String> seenDescriptions = new HashSet<>();
        List<AccountCatalogueExcelData> uniqueRecords = new ArrayList<>();
        int duplicateCount = 0;

        // 1. Obtener códigos y descripciones para consultar en BD
        List<String> codes = accountsData.stream()
                .map(AccountCatalogueExcelData::getCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<String> descriptions = accountsData.stream()
                .map(AccountCatalogueExcelData::getDescription)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(desc -> !desc.isEmpty())
                .distinct()
                .toList();

        // 2. Detectar duplicados en base de datos
        Map<String, AccountCatalogueEntity> dbDuplicatesByCode = detectDatabaseDuplicatesByCode(codes, entId);
        Map<String, AccountCatalogueEntity> dbDuplicatesByDescription = detectDatabaseDuplicatesByDescription(
                descriptions, entId);

        // 3. Procesar cada registro y filtrar duplicados silenciosamente
        for (AccountCatalogueExcelData accountData : accountsData) {
            boolean isDuplicate = isDuplicateRecord(accountData, entId, seenCodes, seenDescriptions,
                    dbDuplicatesByCode, dbDuplicatesByDescription);

            if (isDuplicate) {
                duplicateCount++;
            } else {
                uniqueRecords.add(accountData);
            }
        }

        return DuplicateDetectionResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(new ArrayList<>()) // Sin errores, solo omisión silenciosa
                .totalAnalyzed(accountsData.size())
                .duplicateCount(duplicateCount)
                .uniqueCount(uniqueRecords.size())
                .build();
    }

    /**
     * Detecta duplicados en base de datos por código.
     */
    private Map<String, AccountCatalogueEntity> detectDatabaseDuplicatesByCode(
            List<String> codes, String entId) {
        if (codes == null || codes.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, AccountCatalogueEntity> duplicates = new HashMap<>();

        // Consultar cada código individualmente (no hay método bulk en el repositorio)
        for (String code : codes) {
            try {
                AccountCatalogueEntity existing = accountCatalogueRepository.findByCode(code, entId);
                if (existing != null) {
                    duplicates.put(code, existing);
                }
            } catch (Exception e) {
                throw new AccountCatalogueImportException(
                    AccountCatalogueErrorCode.ACCOUNT_IMPORT_ERROR,
                    "Error al consultar duplicado por código: " + code,
                    e
                );
            }
        }

        return duplicates;
    }

    /**
     * Detecta duplicados en base de datos por descripción.
     * Usa normalización case-insensitive.
     */
    private Map<String, AccountCatalogueEntity> detectDatabaseDuplicatesByDescription(
            List<String> descriptions, String entId) {
        if (descriptions == null || descriptions.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, AccountCatalogueEntity> duplicates = new HashMap<>();

        // Consultar cada descripción individualmente
        for (String description : descriptions) {
            try {
                AccountCatalogueEntity existing = accountCatalogueRepository
                        .findByDescriptionIgnoreCaseAndIdEnterprise(description, entId);
                if (existing != null) {
                    String normalized = StringNormalizer.normalizeForComparison(description);
                    if (normalized == null) {
                        normalized = "";
                    }
                    duplicates.put(normalized, existing);
                }
            } catch (Exception e) {
                throw new AccountCatalogueImportException(
                    AccountCatalogueErrorCode.ACCOUNT_IMPORT_ERROR,
                    "Error al consultar duplicado por descripción: " + description,
                    e
                );
            }
        }

        return duplicates;
    }

    /**
     * Clase que representa el resultado de detección de duplicados.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DuplicateDetectionResult {
        private List<AccountCatalogueExcelData> uniqueRecords;
        private List<ImportErrorDetail> errors;
        private int totalAnalyzed;
        private int duplicateCount;
        private int uniqueCount;
    }
}
