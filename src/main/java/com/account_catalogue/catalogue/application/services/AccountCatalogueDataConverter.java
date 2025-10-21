package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueDataConverter {

    /**
     * Convierte datos de Excel a entidad de dominio AccountCatalogue.
     * 
     * @param excelData datos parseados desde Excel
     * @param parentsMap mapa de códigos padre a entidades padre (para establecer relación)
     * @param processedAccountsMap mapa de cuentas ya procesadas en este lote
     * @return entidad de dominio lista para persistir
     */
    public AccountCatalogue convertToAccountCatalogue(AccountCatalogueExcelData excelData, 
                                                     Map<String, AccountCatalogueEntity> parentsMap,
                                                     Map<String, AccountCatalogue> processedAccountsMap) {
        AccountCatalogue.AccountCatalogueBuilder builder = AccountCatalogue.builder();

        // Campos básicos
        builder.code(normalizeCode(excelData.getCode()));
        builder.description(normalizeDescription(excelData.getDescription()));
        builder.idEnterprise(excelData.getIdEnterprise());

        // Enums
        builder.nature(excelData.getNature());
        builder.financialStatus(excelData.getFinancialStatus());
        builder.classification(excelData.getClassification());

        // Campos booleanos opcionales
        builder.crossing(excelData.getCrossing() != null ? excelData.getCrossing() : false);
        builder.costCenter(excelData.getCostCenter() != null ? excelData.getCostCenter() : false);

        // Estado activo por defecto
        builder.status(true);

        // Establecer parent si corresponde
        AccountCatalogue parent = resolveParent(excelData, parentsMap, processedAccountsMap);
        if (parent != null) {
            builder.parent(parent);
        }

        return builder.build();
    }

    /**
     * Resuelve el padre de una cuenta.
     * Busca primero en las cuentas ya procesadas en este lote,
     * luego en el mapa de padres de la base de datos.
     * CRÍTICO: Retorna el padre con ID para establecer FK correctamente.
     */
    private AccountCatalogue resolveParent(AccountCatalogueExcelData excelData, 
                                          Map<String, AccountCatalogueEntity> parentsMap,
                                          Map<String, AccountCatalogue> processedAccountsMap) {
        String code = excelData.getCode();
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        // Determinar código del padre
        String parentCode = com.account_catalogue.catalogue.domain.utils.AccountCodeUtils.extractParentCode(code);
        if (parentCode == null) {
            // Es cuenta raíz, no tiene padre
            log.debug("Cuenta raíz sin padre: {}", code);
            return null;
        }

        // Buscar primero en cuentas procesadas en este lote
        if (processedAccountsMap.containsKey(parentCode)) {
            AccountCatalogue parent = processedAccountsMap.get(parentCode);
            log.debug("Cuenta '{}' vinculada a padre procesado en lote: código='{}', id={}", 
                    code, parentCode, parent.getId());
            return parent;
        }

        // Buscar en el mapa de padres de la BD
        if (parentsMap.containsKey(parentCode)) {
            AccountCatalogue parent = convertEntityToDomain(parentsMap.get(parentCode));
            log.debug("Cuenta '{}' vinculada a padre existente en BD: código='{}', id={}", 
                    code, parentCode, parent != null ? parent.getId() : "null");
            return parent;
        }

        // No se encontró el padre (esto no debería pasar si la validación jerárquica funcionó)
        log.error("CRÍTICO: No se encontró el padre '{}' para la cuenta '{}' en fila {}", 
                parentCode, code, excelData.getRowNumber());
        return null;
    }

    /**
     * Convierte una entidad JPA a modelo de dominio (solo campos básicos para referencia).
     * IMPORTANTE: Incluye el ID para que las cuentas hijas puedan vincularse correctamente.
     */
    private AccountCatalogue convertEntityToDomain(AccountCatalogueEntity entity) {
        if (entity == null) {
            log.warn("Intento de convertir entidad null a dominio");
            return null;
        }

        if (entity.getId() == null) {
            log.error("CRÍTICO: Entidad sin ID - código: {}", entity.getCode());
        }

        AccountCatalogue.AccountCatalogueBuilder builder = AccountCatalogue.builder()
                .id(entity.getId())  // CRÍTICO: ID necesario para FK
                .code(entity.getCode())
                .description(entity.getDescription())
                .nature(entity.getNature())
                .financialStatus(entity.getFinancialStatus())
                .classification(entity.getClassification())
                .crossing(entity.getCrossing())
                .costCenter(entity.getCostCenter())
                .status(entity.getStatus())
                .idEnterprise(entity.getIdEnterprise());        
        
        // Incluir parent si existe (para jerarquías anidadas)
        if (entity.getParent() != null) {
            builder.parent(AccountCatalogue.builder()
                    .id(entity.getParent().getId())
                    .code(entity.getParent().getCode())
                    .build());
        }
        
        AccountCatalogue result = builder.build();
        log.debug("Entidad convertida: código={}, id={}, tiene parent={}", 
                result.getCode(), result.getId(), result.getParent() != null);
        
        return result;
    }

    /**
     * Normaliza el código de cuenta (trim).
     */
    private String normalizeCode(String code) {
        if (code == null) {
            return null;
        }
        return code.trim();
    }

    /**
     * Normaliza la descripción de cuenta (trim y espacios múltiples).
     */
    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        
        // Trim y eliminar espacios múltiples
        return description.trim().replaceAll("\\s+", " ");
    }
}

