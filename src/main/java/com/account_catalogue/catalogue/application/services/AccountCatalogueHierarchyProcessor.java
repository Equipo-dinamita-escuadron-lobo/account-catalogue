package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.utils.AccountCodeUtils;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueHierarchyException;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en el procesamiento jerárquico de cuentas contables.
 * Ordena cuentas por jerarquía y valida que no existan cuentas huérfanas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueHierarchyProcessor {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    /**
     * Ordena y valida la jerarquía de cuentas.
     * Asegura que todas las cuentas tengan su padre correspondiente.
     * 
     * @param accountsData lista de cuentas a procesar
     * @param entId identificador de la empresa
     * @return lista ordenada por jerarquía
     * @throws AccountCatalogueHierarchyException si existen cuentas huérfanas
     */
    public List<AccountCatalogueExcelData> sortAndValidateHierarchy(List<AccountCatalogueExcelData> accountsData, 
                                                                     String entId) {
        if (accountsData == null || accountsData.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Ordenar por jerarquía (código natural)
        List<AccountCatalogueExcelData> sortedAccounts = AccountCodeUtils.sortByHierarchy(accountsData);

        // 2. Validar que no haya cuentas huérfanas
        validateHierarchy(sortedAccounts, entId);

        return sortedAccounts;
    }

    /**
     * Valida que todas las cuentas tengan su padre correspondiente.
     * El padre puede estar en el Excel o en la base de datos.
     */
    private void validateHierarchy(List<AccountCatalogueExcelData> accountsData, String entId) {
        // Construir mapa de cuentas en el Excel para búsqueda rápida
        Map<String, AccountCatalogueExcelData> accountMap = buildHierarchyMap(accountsData);

        // Lista de errores de jerarquía
        List<String> orphanErrors = new ArrayList<>();

        for (AccountCatalogueExcelData account : accountsData) {
            String code = account.getCode();
            
            // Si es cuenta raíz (1 dígito), no necesita padre
            if (AccountCodeUtils.isRootAccount(code)) {
                continue;
            }

            // Obtener código del padre
            String parentCode = AccountCodeUtils.extractParentCode(code);
            if (parentCode == null) {
                // No debería llegar aquí si no es cuenta raíz, pero por seguridad
                continue;
            }

            // Verificar si el padre existe en el Excel
            boolean parentInExcel = accountMap.containsKey(parentCode);

            // Si no está en Excel, verificar si está en BD
            boolean parentInDatabase = false;
            if (!parentInExcel) {
                parentInDatabase = existsInDatabase(parentCode, entId);
            }

            // Si no está en ningún lugar, es cuenta huérfana
            if (!parentInExcel && !parentInDatabase) {
                orphanErrors.add(String.format(
                    "Fila %d: La cuenta '%s' requiere una cuenta padre '%s' que no existe ni en el Excel ni en el sistema",
                    account.getRowNumber(), code, parentCode
                ));
            }
        }

        // Si hay errores de jerarquía, lanzar excepción
        if (!orphanErrors.isEmpty()) {
            String errorMessage = "Se encontraron cuentas huérfanas:\n" + 
                    String.join("\n", orphanErrors);
            throw new AccountCatalogueHierarchyException(errorMessage);
        }
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
     * 
     * @param codes códigos de cuentas que potencialmente necesitan padres
     * @param entId identificador de la empresa
     * @return mapa de código -> entidad de cuenta padre
     */
    public Map<String, AccountCatalogueEntity> buildParentMapFromDatabase(Set<String> codes, String entId) {
        Map<String, AccountCatalogueEntity> parentMap = new HashMap<>();

        for (String code : codes) {
            String parentCode = AccountCodeUtils.extractParentCode(code);
            if (parentCode != null && !parentMap.containsKey(parentCode)) {
                try {
                    AccountCatalogueEntity parent = accountCatalogueRepository.findByCode(parentCode, entId);
                    if (parent != null) {
                        parentMap.put(parentCode, parent);
                    }
                } catch (Exception e) {
                    log.warn("Error obteniendo cuenta padre {}: {}", parentCode, e.getMessage());
                }
            }
        }

        return parentMap;
    }
}

