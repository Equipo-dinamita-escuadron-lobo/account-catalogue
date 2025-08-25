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
        // Validar el código de cuenta
        validationService.validateAccountCode(accountCatalogue.getCode());
        
        // Validar la descripción de la cuenta
        validationService.validateAccountDescription(accountCatalogue.getDescription());
        
        // Validar que la cuenta no exista ya por código
        validationService.validateAccountDoesNotExist(
            accountCatalogue.getCode(), 
            accountCatalogue.getIdEnterprise()
        );
        
        // Validar que no exista ya una cuenta con la misma descripción
        validationService.validateAccountDescriptionDoesNotExist(
            accountCatalogue.getDescription(), 
            accountCatalogue.getIdEnterprise()
        );
        
        // Si tiene padre, validar que el padre existe y no está eliminado
        if (accountCatalogue.getParent() != null && accountCatalogue.getParent().getId() != null) {
            AccountCatalogue parent = validationService.validateAccountExistsById(accountCatalogue.getParent().getId());
            // El método validateAccountExistsById ya valida que no esté eliminada
        }
        
        // Validar que crossing y costCenter solo se puedan establecer en cuentas auxiliares (8 dígitos)
        validationService.validateCrossingAndCostCenterOnlyForAuxiliaryAccounts(accountCatalogue);
        
        return accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);
    }
}
