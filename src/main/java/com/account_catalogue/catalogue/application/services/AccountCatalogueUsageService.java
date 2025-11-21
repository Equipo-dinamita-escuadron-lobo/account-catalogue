package com.account_catalogue.catalogue.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueUsagePort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueUpdateOutputPort;
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

    @Override
    public void incrementUsageCount(Long accountCatalogueId, String enterpriseId) {
        log.info("Incrementing usage count for accountCatalogueId: {} in enterprise: {}", accountCatalogueId, enterpriseId);

        AccountCatalogue accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueById(accountCatalogueId, enterpriseId);
        if (accountCatalogue == null) {
            log.warn("AccountCatalogue not found: {} in enterprise: {}", accountCatalogueId, enterpriseId);
            throw new AccountCatalogueNotFoundException("Cuenta contable no encontrada: " + accountCatalogueId);
        }

        accountCatalogue.incrementUsageCount();
        accountCatalogueUpdateOutputPort.updateAccountCatalogue(accountCatalogueId, accountCatalogue);

        log.info("Usage count incremented successfully for accountCatalogueId: {}. New count: {}",
                 accountCatalogueId, accountCatalogue.getUsageCount());
    }
}

