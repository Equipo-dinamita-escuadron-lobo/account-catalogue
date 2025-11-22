package com.account_catalogue.catalogue.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueUsagePort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.dto.AccountUsedEventDto;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Servicio para gestión de uso de cuentas contables
 *
 * Maneja la lógica de negocio relacionada con el contador de uso de cuentas contables
 * cuando son utilizados por otros servicios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCatalogueUsageService implements IAccountCatalogueUsagePort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final IAccountCatalogueUpdateOutputPort accountCatalogueUpdateOutputPort;

    private static final String ACCOUNT_TYPE_ID = "ID";
    private static final String ACCOUNT_TYPE_CODE = "CODE";

    @Override
    public void incrementUsageCount(AccountUsedEventDto accountUsedEvent) {

        String sourceAccountType = accountUsedEvent.getSourceAccountType();
        Long account = accountUsedEvent.getAccount();
        String enterpriseId = accountUsedEvent.getEnterpriseId();

        log.info("=== INCREMENT USAGE COUNT START ===");
        log.info("Event data - Account: {}, SourceAccountType: '{}', EnterpriseId: '{}'",
                 account, sourceAccountType, enterpriseId);

        log.info("Incrementing usage count for account: {} (type: {}) in enterprise: {}",
                 account, sourceAccountType, enterpriseId);

        AccountCatalogue accountCatalogue;

        // Determinar si buscar por ID o código
        if (ACCOUNT_TYPE_ID.equalsIgnoreCase(sourceAccountType)) {
            log.info("Searching account by ID: {} in enterprise: {}", account, enterpriseId);
            accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueById(account, enterpriseId);
            log.info("Account found by ID: {}", accountCatalogue != null ? "YES (ID: " + accountCatalogue.getId() + ", Code: " + accountCatalogue.getCode() + ")" : "NO");
        } else if (ACCOUNT_TYPE_CODE.equalsIgnoreCase(sourceAccountType)) {
            String codeString = account.toString();
            log.info("Searching account by CODE: '{}' in enterprise: {}", codeString, enterpriseId);
            accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(codeString, enterpriseId);
            log.info("Account found by CODE: {}", accountCatalogue != null ? "YES (ID: " + accountCatalogue.getId() + ", Code: " + accountCatalogue.getCode() + ")" : "NO");
        } else {
            log.error("Invalid sourceAccountType: '{}'. Must be '{}' or '{}'", sourceAccountType, ACCOUNT_TYPE_ID, ACCOUNT_TYPE_CODE);
            throw new IllegalArgumentException("Tipo de fuente de cuenta inválido: " + sourceAccountType + ". Debe ser '" + ACCOUNT_TYPE_ID + "' o '" + ACCOUNT_TYPE_CODE + "'");
        }

        if (accountCatalogue == null) {
            log.warn("AccountCatalogue not found: {} (type: {}) in enterprise: {}", account, sourceAccountType, enterpriseId);
            throw new AccountCatalogueNotFoundException("Cuenta contable no encontrada: " + account + " (tipo: " + sourceAccountType + ")");
        }

        log.info("Before increment - Current usage count: {}", accountCatalogue.getUsageCount());
        accountCatalogue.incrementUsageCount();
        log.info("After increment - New usage count: {}", accountCatalogue.getUsageCount());

        log.info("Updating account in database...");
        AccountCatalogue updatedAccount = accountCatalogueUpdateOutputPort.updateAccountCatalogue(accountCatalogue.getId(), accountCatalogue);

        if (updatedAccount != null) {
            log.info("=== INCREMENT USAGE COUNT SUCCESS ===");
            log.info("Final usage count in updated account: {}", updatedAccount.getUsageCount());
        } else {
            log.error("=== INCREMENT USAGE COUNT FAILED ===");
            log.error("Update operation returned null");
        }
    }
}

