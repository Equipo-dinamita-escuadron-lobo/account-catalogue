package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.utils.AccountCodeUtils;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueHierarchyProcessor {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    /**
     * Ordena cuentas por jerarquía.
     * IMPORTANTE: Permite importar solo cuentas hijas si sus padres ya existen en el sistema.
     * 
     * @param accountsData lista de cuentas a procesar
     * @return lista ordenada por jerarquía
     */
    public List<AccountCatalogueExcelData> sortByHierarchy(List<AccountCatalogueExcelData> accountsData) {
        if (accountsData == null || accountsData.isEmpty()) {
            return new ArrayList<>();
        }

        // Ordenar por jerarquía (código natural)
        return AccountCodeUtils.sortByHierarchy(accountsData);
    }

    /**
     * Valida que todas las cuentas tengan su padre correspondiente.
     * El padre puede estar en el Excel o en la base de datos.
     * Esto permite importar solo cuentas hijas que se vincularán a padres existentes en el sistema.
     * 
     * @return lista de errores individuales para cada cuenta huérfana
     */
    public List<com.account_catalogue.catalogue.domain.models.ImportErrorDetail> validateHierarchyWithDetails(
            List<AccountCatalogueExcelData> accountsData, String entId) {
        List<com.account_catalogue.catalogue.domain.models.ImportErrorDetail> errors = new ArrayList<>();
        
        // Construir mapa de cuentas en el Excel para búsqueda rápida
        Map<String, AccountCatalogueExcelData> accountMap = buildHierarchyMap(accountsData);

        for (AccountCatalogueExcelData account : accountsData) {
            String code = account.getCode();
            
            // Si es cuenta raíz (1 dígito), no necesita padre
            if (AccountCodeUtils.isRootAccount(code)) {
                log.debug("Cuenta raíz detectada: {}", code);
                continue;
            }

            // Obtener código del padre
            String parentCode = AccountCodeUtils.extractParentCode(code);
            if (parentCode == null) {
                // No debería llegar aquí si no es cuenta raíz, pero por seguridad
                log.warn("No se pudo extraer código padre para cuenta: {}", code);
                continue;
            }

            // Verificar si el padre existe en el Excel
            boolean parentInExcel = accountMap.containsKey(parentCode);

            // Si no está en Excel, verificar si está en BD
            boolean parentInDatabase = false;
            if (!parentInExcel) {
                parentInDatabase = existsInDatabase(parentCode, entId);
                if (parentInDatabase) {
                    log.debug("Cuenta hija '{}' se vinculará a padre existente en BD: '{}'", 
                            code, parentCode);
                }
            }

            // Si no está en ningún lugar, crear error individual
            if (!parentInExcel && !parentInDatabase) {
                errors.add(com.account_catalogue.catalogue.domain.models.ImportErrorDetail.builder()
                        .rowNumber(account.getRowNumber())
                        .columnNumber(1) // Columna de Código
                        .columnName("Código")
                        .fieldValue(code)
                        .errorCode(com.account_catalogue.catalogue.domain.utils.ImportConstants.ErrorCodes.ORPHAN_ACCOUNT)
                        .errorMessage(String.format(
                                "La cuenta '%s' requiere una cuenta padre '%s' que no existe ni en el Excel ni en el sistema",
                                code, parentCode))
                        .errorType(com.account_catalogue.catalogue.domain.enums.ImportErrorType.HIERARCHY_ERROR)
                        .build());
            }
        }
        
        if (errors.isEmpty()) {
            log.info("Validación de jerarquía exitosa: {} cuentas validadas", accountsData.size());
        } else {
            log.warn("Se encontraron {} cuentas huérfanas", errors.size());
        }
        
        return errors;
    }

    /**
     * Construye un mapa de códigos para búsqueda rápida.
     */
    private Map<String, AccountCatalogueExcelData> buildHierarchyMap(List<AccountCatalogueExcelData> accountsData) {
        return accountsData.stream()
                .filter(account -> account.getCode() != null && !account.getCode().trim().isEmpty())
                .collect(Collectors.toMap(
                        account -> account.getCode().trim(),
                        account -> account,
                        (existing, replacement) -> existing // En caso de duplicados, mantener el primero
                ));
    }

    /**
     * Verifica si una cuenta existe en la base de datos.
     */
    private boolean existsInDatabase(String code, String entId) {
        try {
            AccountCatalogueEntity existing = accountCatalogueRepository.findByCode(code, entId);
            return existing != null;
        } catch (Exception e) {
            log.warn("Error verificando existencia de cuenta padre {}: {}", code, e.getMessage());
            return false;
        }
    }

    /**
     * Crea un mapa de padres desde la base de datos para las cuentas dadas.
     * Útil para el procesamiento por lotes cuando se necesitan los padres.
     * IMPORTANTE: Permite vincular cuentas hijas importadas a padres existentes en el sistema.
     * 
     * @param parentCodes códigos de los PADRES que se necesitan buscar en BD
     * @param entId identificador de la empresa
     * @return mapa de código padre -> entidad de cuenta padre
     */
    public Map<String, AccountCatalogueEntity> buildParentMapFromDatabase(Set<String> parentCodes, String entId) {
        Map<String, AccountCatalogueEntity> parentMap = new HashMap<>();

        log.debug("Buscando {} códigos padre en BD: {}", parentCodes.size(), parentCodes);

        for (String parentCode : parentCodes) {
            if (parentCode != null && !parentMap.containsKey(parentCode)) {
                try {
                    AccountCatalogueEntity parent = accountCatalogueRepository.findByCode(parentCode, entId);
                    if (parent != null) {
                        parentMap.put(parentCode, parent);
                        log.info("✓ Padre de BD cargado: código='{}', id={}", parentCode, parent.getId());
                    } else {
                        log.warn("✗ Padre NO encontrado en BD: código='{}'", parentCode);
                    }
                } catch (Exception e) {
                    log.error("✗ Error obteniendo cuenta padre '{}': {}", parentCode, e.getMessage(), e);
                }
            }
        }

        log.info("Mapa de padres construido: {} de {} padres encontrados en BD", parentMap.size(), parentCodes.size());
        return parentMap;
    }
}

