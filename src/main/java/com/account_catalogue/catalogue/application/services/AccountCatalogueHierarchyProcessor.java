package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ImportErrorType;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;
import com.account_catalogue.catalogue.domain.models.ImportErrorDetail;
import com.account_catalogue.catalogue.domain.utils.AccountCodeUtils;
import com.account_catalogue.catalogue.domain.utils.ImportConstants;
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

    public List<AccountCatalogueExcelData> sortByHierarchy(List<AccountCatalogueExcelData> accountsData) {
        if (accountsData == null || accountsData.isEmpty()) {
            return new ArrayList<>();
        }

        // Ordenar por jerarquía (código natural)
        return AccountCodeUtils.sortByHierarchy(accountsData);
    }

    public List<ImportErrorDetail> validateHierarchyWithDetails(
            List<AccountCatalogueExcelData> accountsData, String entId) {
        List<ImportErrorDetail> errors = new ArrayList<>();

        Map<String, AccountCatalogueExcelData> accountMap = buildHierarchyMap(accountsData);

        for (AccountCatalogueExcelData account : accountsData) {
            String code = account.getCode();

            // Si es cuenta raíz (1 dígito), no necesita padre
            if (AccountCodeUtils.isRootAccount(code)) {
                continue;
            }

            String parentCode = AccountCodeUtils.extractParentCode(code);
            if (parentCode == null) {
                continue;
            }

            boolean parentInExcel = accountMap.containsKey(parentCode);

            boolean parentInDatabase = false;
            if (!parentInExcel) {
                parentInDatabase = existsInDatabase(parentCode, entId);
            }

            if (!parentInExcel && !parentInDatabase) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(account.getRowNumber())
                        .columnNumber(1)
                        .columnName("Código")
                        .fieldValue(code)
                        .errorCode(ImportConstants.ErrorCodes.ORPHAN_ACCOUNT)
                        .errorMessage(String.format(
                                "La cuenta '%s' requiere una cuenta padre '%s' que no existe ni en el Excel ni en el sistema",
                                code, parentCode))
                        .errorType(ImportErrorType.HIERARCHY_ERROR)
                        .build());
            }
        }

        return errors;
    }

    private Map<String, AccountCatalogueExcelData> buildHierarchyMap(List<AccountCatalogueExcelData> accountsData) {
        return accountsData.stream()
                .filter(account -> account.getCode() != null && !account.getCode().trim().isEmpty())
                .collect(Collectors.toMap(
                        account -> account.getCode().trim(),
                        account -> account,
                        (existing, replacement) -> existing // En caso de duplicados, mantener el primero
                ));
    }

    private boolean existsInDatabase(String code, String entId) {
        try {
            AccountCatalogueEntity existing = accountCatalogueRepository.findByCode(code, entId);
            return existing != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, AccountCatalogueEntity> buildParentMapFromDatabase(Set<String> parentCodes, String entId) {
        Map<String, AccountCatalogueEntity> parentMap = new HashMap<>();

        for (String parentCode : parentCodes) {
            if (parentCode != null && !parentMap.containsKey(parentCode)) {
                try {
                    AccountCatalogueEntity parent = accountCatalogueRepository.findByCode(parentCode, entId);
                    if (parent != null) {
                        parentMap.put(parentCode, parent);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error al obtener cuenta padre", e);
                }
            }
        }

        return parentMap;
    }
}
