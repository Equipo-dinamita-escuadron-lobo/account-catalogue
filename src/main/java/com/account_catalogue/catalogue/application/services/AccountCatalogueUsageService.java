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

        log.info("Incrementing usage count for account: {} (type: {}) in enterprise: {}",
                 account, sourceAccountType, enterpriseId);

        AccountCatalogue accountCatalogue;

        // Determinar si buscar por ID o código
        if (ACCOUNT_TYPE_ID.equalsIgnoreCase(sourceAccountType)) {
            accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueById(account, enterpriseId);
        } else if (ACCOUNT_TYPE_CODE.equalsIgnoreCase(sourceAccountType)) {
            accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(account.toString(), enterpriseId);
        } else {
            log.error("Invalid sourceAccountType: {}. Must be '{}' or '{}'", sourceAccountType, ACCOUNT_TYPE_ID, ACCOUNT_TYPE_CODE);
            throw new IllegalArgumentException("Tipo de fuente de cuenta inválido: " + sourceAccountType + ". Debe ser '" + ACCOUNT_TYPE_ID + "' o '" + ACCOUNT_TYPE_CODE + "'");
        }

        if (accountCatalogue == null) {
            log.warn("AccountCatalogue not found: {} (type: {}) in enterprise: {}", account, sourceAccountType, enterpriseId);
            throw new AccountCatalogueNotFoundException("Cuenta contable no encontrada: " + account + " (tipo: " + sourceAccountType + ")");
        }

        accountCatalogue.incrementUsageCount();
        accountCatalogueUpdateOutputPort.updateAccountCatalogue(accountCatalogue.getId(), accountCatalogue);

        log.info("Usage count incremented successfully for account: {} (type: {}). New count: {}",
                 account, sourceAccountType, accountCatalogue.getUsageCount());
    }
}

