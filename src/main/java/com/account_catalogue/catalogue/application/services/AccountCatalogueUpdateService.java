package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueUpdateService implements IAccountCatalogueUpdateInputPort {

    private final IAccountCatalogueUpdateOutputPort accountCatalogueUpdateOutputport;
    private final AccountCatalogueValidationService validationService;

    /**
     * Actualiza un catálogo de cuenta con validaciones.
     * Permite actualizar cuentas que tienen hijos, pero valida que no esté asociada a impuestos.
     *
     * @param id El id del catálogo de cuenta a actualizar.
     * @param accountCatalogue El catálogo de cuenta actualizado.
     * @return El catálogo de cuenta actualizado.
     */
    @Override
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {
        // Validar que la cuenta a actualizar existe
        AccountCatalogue existingAccount = validationService.validateAccountExistsById(id);
        
        // Verificar que el idEnterprise esté establecido
        if (accountCatalogue.getIdEnterprise() == null || accountCatalogue.getIdEnterprise().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para la actualización");
        }
        
        // Validar que el idEnterprise coincida con el de la cuenta existente (seguridad)
        if (!accountCatalogue.getIdEnterprise().equals(existingAccount.getIdEnterprise())) {
            throw new IllegalArgumentException(
                "El ID de empresa no coincide. Esperado: " + existingAccount.getIdEnterprise() + 
                ", Recibido: " + accountCatalogue.getIdEnterprise()
            );
        }
        
        // Normalizar y validar el código de cuenta
        if (accountCatalogue.getCode() != null) {
            accountCatalogue.setCode(accountCatalogue.getCode().trim());
        }
        validationService.validateAccountCode(accountCatalogue.getCode());
        
        // Normalizar y validar la descripción de la cuenta
        if (accountCatalogue.getDescription() != null) {
            accountCatalogue.setDescription(accountCatalogue.getDescription().trim());
        }
        validationService.validateAccountDescription(accountCatalogue.getDescription());
        
        // Validar que no existe otra cuenta con el mismo código (excluyendo la actual)
        validationService.validateAccountDoesNotExistExcluding(
            accountCatalogue.getCode(), 
            accountCatalogue.getIdEnterprise(), 
            id
        );
        
        // Validar que no existe otra cuenta con la misma descripción (excluyendo la actual) - case-insensitive
        validationService.validateAccountDescriptionDoesNotExistExcluding(
            accountCatalogue.getDescription(), 
            accountCatalogue.getIdEnterprise(), 
            id
        );
        
        // Solo validar que la cuenta no está asociada a impuestos
        // (Se permite actualizar cuentas que tienen hijos)
        validationService.validateAccountNotAssociatedWithTaxes(existingAccount);
        
        // Si tiene padre, validar que el padre existe
        if (accountCatalogue.getParent() != null && accountCatalogue.getParent().getId() != null) {
            validationService.validateAccountExistsById(accountCatalogue.getParent().getId());
        }
        
        // Validar que crossing y costCenter solo se puedan establecer en cuentas auxiliares (8 dígitos)
        validationService.validateCrossingAndCostCenterOnlyForAuxiliaryAccounts(accountCatalogue);
        
        // Validar que costCenter solo pueda ser true cuando financialStatus sea Estado de Resultados
        validationService.validateCostCenterRequiresIncomeStatement(accountCatalogue);
        
        return accountCatalogueUpdateOutputport.updateAccountCatalogue(id, accountCatalogue);
    }
}
