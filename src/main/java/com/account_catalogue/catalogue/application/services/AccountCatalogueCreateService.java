package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.services.AccountCatalogueValidationService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueCreateService implements IAccountCatalogueCreateInputPort {

    private final IAccountCatalogueCreateOutputPort accountCatalogueCreateOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Crea un catálogo de cuenta con validaciones completas.
     *
     * @param accountCatalogue El catálogo de cuenta a crear.
     * @return El catálogo de cuenta creado.
     */
    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {
        // Validar el código de cuenta
        validationService.validateAccountCode(accountCatalogue.getCode());
        
        // Validar la descripción de la cuenta
        validationService.validateAccountDescription(accountCatalogue.getDescription());
        
        // Validar que la cuenta no exista ya
        validationService.validateAccountDoesNotExist(
            accountCatalogue.getCode(), 
            accountCatalogue.getIdEnterprise()
        );
        
        // Si tiene padre, validar que el padre existe
        if (accountCatalogue.getParent() != null && accountCatalogue.getParent().getId() != null) {
            validationService.validateAccountExistsById(accountCatalogue.getParent().getId());
        }
        
        return accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);
    }
}
