package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueDuplicateDetectionService {

    private final IAccountCatalogueRepository accountCatalogueRepository;

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
                .collect(Collectors.toList());

        List<String> descriptions = accountsData.stream()
                .map(AccountCatalogueExcelData::getDescription)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(desc -> !desc.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // 2. Detectar duplicados en base de datos
        Map<String, AccountCatalogueEntity> dbDuplicatesByCode = detectDatabaseDuplicatesByCode(codes, entId);
        Map<String, AccountCatalogueEntity> dbDuplicatesByDescription = detectDatabaseDuplicatesByDescription(
                descriptions, entId);

        // 3. Procesar cada registro y filtrar duplicados silenciosamente
        for (AccountCatalogueExcelData record : accountsData) {
            String code = record.getCode();
            String description = record.getDescription();
            String normalizedDescription = StringNormalizer.normalizeForComparison(description);
            if (normalizedDescription == null) {
                normalizedDescription = "";
            }
            String codeKey = code + "_" + entId;
            String descKey = normalizedDescription + "_" + entId;

            boolean isDuplicate = false;

            if (seenCodes.contains(codeKey)) {
                isDuplicate = true;
            } else {
                seenCodes.add(codeKey);
            }

            if (!isDuplicate && seenDescriptions.contains(descKey)) {
                isDuplicate = true;
            } else if (!isDuplicate) {
                seenDescriptions.add(descKey);
            }

            if (!isDuplicate && dbDuplicatesByCode.containsKey(code)) {
                isDuplicate = true;
            }

            if (!isDuplicate && dbDuplicatesByDescription.containsKey(normalizedDescription)) {
                isDuplicate = true;
            }

            if (isDuplicate) {
                duplicateCount++;
            } else {
                uniqueRecords.add(record);
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
                throw new RuntimeException("Error al consultar duplicado por código", e);
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
                throw new RuntimeException("Error al consultar duplicado por descripción", e);
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
