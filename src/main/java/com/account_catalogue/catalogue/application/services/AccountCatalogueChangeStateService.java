package com.account_catalogue.catalogue.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueChangeStateInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueChangeStateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountCatalogueChangeStateService implements IAccountCatalogueChangeStateInputPort {

    private final IAccountCatalogueChangeStateOutputPort accountCatalogueChangeStateOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Cambia el estado (activo/inactivo) de una cuenta del catálogo y todos sus descendientes.
     * Valida que la cuenta exista antes de cambiar su estado.
     *
     * @param id el ID de la cuenta
     * @param idEnterprise el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return la cuenta actualizada
     */
    @Transactional
    @Override
    public AccountCatalogue changeState(Long id, String idEnterprise, Boolean status) {
        
        validationService.validateAccountExistsByIdAndEnterprise(id, idEnterprise);

        return accountCatalogueChangeStateOutputPort.changeState(id, status);
    }
}
