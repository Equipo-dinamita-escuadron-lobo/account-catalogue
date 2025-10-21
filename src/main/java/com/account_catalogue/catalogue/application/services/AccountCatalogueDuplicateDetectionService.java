package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en detección de duplicados para importación de catálogo de cuentas.
 * Detecta duplicados tanto internos (dentro del Excel) como en la base de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueDuplicateDetectionService {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    /**
     * Detecta duplicados internos en el Excel y contra la base de datos.
     * Los duplicados se detectan por código Y descripción (case-insensitive sin acentos).
     */
    public DuplicateDetectionResult detectDuplicates(List<AccountCatalogueExcelData> accountsData, String entId) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();
        Set<String> seenDescriptions = new HashSet<>();
        List<AccountCatalogueExcelData> uniqueRecords = new ArrayList<>();

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
        Map<String, AccountCatalogueEntity> dbDuplicatesByCode = 
                detectDatabaseDuplicatesByCode(codes, entId);
        Map<String, AccountCatalogueEntity> dbDuplicatesByDescription = 
                detectDatabaseDuplicatesByDescription(descriptions, entId);

        // 3. Procesar cada registro y filtrar duplicados
        for (AccountCatalogueExcelData record : accountsData) {
            String code = record.getCode();
            String description = record.getDescription();
            String normalizedDescription = normalizeString(description);
            String codeKey = code + "_" + entId;
            String descKey = normalizedDescription + "_" + entId;

            boolean isDuplicate = false;

            // Verificar duplicado interno por código
            if (seenCodes.contains(codeKey)) {
                errors.add(createDuplicateError(record.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                        "Código duplicado dentro del archivo Excel"));
                isDuplicate = true;
            } else {
                seenCodes.add(codeKey);
            }

            // Verificar duplicado interno por descripción
            if (seenDescriptions.contains(descKey)) {
                errors.add(createDuplicateError(record.getRowNumber(), ImportConstants.NAME_COLUMN, description,
                        "Descripción duplicada dentro del archivo Excel"));
                isDuplicate = true;
            } else {
                seenDescriptions.add(descKey);
            }

            // Verificar duplicado en BD por código
            if (dbDuplicatesByCode.containsKey(code)) {
                errors.add(createDuplicateError(record.getRowNumber(), ImportConstants.CODE_COLUMN, code,
                        String.format("El código '%s' ya existe en el sistema", code)));
                isDuplicate = true;
            }

            // Verificar duplicado en BD por descripción
            if (dbDuplicatesByDescription.containsKey(normalizedDescription)) {
                AccountCatalogueEntity existingAccount = dbDuplicatesByDescription.get(normalizedDescription);
                errors.add(createDuplicateError(record.getRowNumber(), ImportConstants.NAME_COLUMN, description,
                        String.format("La descripción '%s' ya existe en el sistema (código: %s)", 
                                description, existingAccount.getCode())));
                isDuplicate = true;
            }

            // Solo agregar a únicos si no es duplicado
            if (!isDuplicate) {
                uniqueRecords.add(record);
            }
        }

        return DuplicateDetectionResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(errors)
                .totalAnalyzed(accountsData.size())
                .duplicateCount(accountsData.size() - uniqueRecords.size())
                .uniqueCount(uniqueRecords.size())
                .build();
    }

    /**
     * Detecta duplicados internos por código dentro del Excel.
     */
    private Map<String, AccountCatalogueExcelData> detectInternalDuplicatesByCode(
            List<AccountCatalogueExcelData> accountsData) {
        Map<String, AccountCatalogueExcelData> firstOccurrences = new HashMap<>();
        Map<String, AccountCatalogueExcelData> duplicates = new HashMap<>();

        for (AccountCatalogueExcelData record : accountsData) {
            String code = record.getCode();
            if (code == null || code.trim().isEmpty()) {
                continue;
            }

            if (firstOccurrences.containsKey(code)) {
                duplicates.put(code, record);
            } else {
                firstOccurrences.put(code, record);
            }
        }

        return duplicates;
    }

    /**
     * Detecta duplicados internos por descripción dentro del Excel.
     * Usa normalización case-insensitive sin acentos.
     */
    private Map<String, AccountCatalogueExcelData> detectInternalDuplicatesByDescription(
            List<AccountCatalogueExcelData> accountsData) {
        Map<String, AccountCatalogueExcelData> firstOccurrences = new HashMap<>();
        Map<String, AccountCatalogueExcelData> duplicates = new HashMap<>();

        for (AccountCatalogueExcelData record : accountsData) {
            String description = record.getDescription();
            if (description == null || description.trim().isEmpty()) {
                continue;
            }

            String normalized = normalizeString(description);
            if (firstOccurrences.containsKey(normalized)) {
                duplicates.put(normalized, record);
            } else {
                firstOccurrences.put(normalized, record);
            }
        }

        return duplicates;
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
                log.warn("Error verificando duplicado para código {}: {}", code, e.getMessage());
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
                    String normalized = normalizeString(description);
                    duplicates.put(normalized, existing);
                }
            } catch (Exception e) {
                log.warn("Error verificando duplicado para descripción {}: {}", description, e.getMessage());
            }
        }

        return duplicates;
    }

    /**
     * Normaliza un string removiendo acentos y convirtiendo a minúsculas.
     * Usado para comparaciones case-insensitive sin acentos.
     */
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }

        // Eliminar espacios extra y trim
        String normalized = input.trim().replaceAll("\\s+", " ");

        // Convertir a minúsculas
        normalized = normalized.toLowerCase();

        // Remover acentos
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        return normalized;
    }

    /**
     * Crea un error de duplicado.
     */
    private ImportErrorDetail createDuplicateError(int rowNumber, String columnName, 
                                                   String fieldValue, String message) {
        return ImportErrorDetail.builder()
                .rowNumber(rowNumber)
                .columnName(columnName)
                .fieldValue(fieldValue)
                .errorCode(ImportConstants.ErrorCodes.DUPLICATE_CODE)
                .errorMessage(message)
                .errorType(ImportErrorType.DUPLICATE_ERROR)
                .build();
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

