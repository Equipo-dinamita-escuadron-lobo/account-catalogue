package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

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
        
        if (accountCatalogue.getCode() != null) {
            accountCatalogue.setCode(accountCatalogue.getCode().trim());
        }
        validationService.validateAccountCode(accountCatalogue.getCode());
        
        if (accountCatalogue.getDescription() != null) {
            accountCatalogue.setDescription(accountCatalogue.getDescription().trim());
        }
        validationService.validateAccountDescription(accountCatalogue.getDescription());
        
        validationService.validateAccountDoesNotExist(
            accountCatalogue.getCode(), 
            accountCatalogue.getIdEnterprise()
        );
        
        validationService.validateAccountDescriptionDoesNotExist(
            accountCatalogue.getDescription(), 
            accountCatalogue.getIdEnterprise()
        );
        
        if (accountCatalogue.getParent() != null && accountCatalogue.getParent().getId() != null) {
            validationService.validateAccountExistsByIdAndEnterprise(accountCatalogue.getParent().getId(), accountCatalogue.getIdEnterprise());
            
        }
        
        validationService.validateCrossingAndCostCenterOnlyForAuxiliaryAccounts(accountCatalogue);
        
        validationService.validateCostCenterRequiresIncomeStatement(accountCatalogue);
        
        return accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);
    }
}
