package com.account_catalogue.catalogue.application.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Realiza un soft delete de un catálogo de cuenta por su ID para una empresa específica.
     * Marca la cuenta como eliminada (isDeleted = true) sin eliminarla físicamente.
     * Valida que no esté asociada a impuestos antes de marcar como eliminada.
     * 
     * @param id ID del catálogo de cuenta a marcar como eliminada
     * @param idEnterprise ID de la empresa para la cual marcar la cuenta como eliminada
     */
    @Transactional
    @Override
    public void deleteById(Long id, String idEnterprise) {
        // Validar que la cuenta existe para la empresa específica
        AccountCatalogue accountToDelete = validationService.validateAccountExistsByIdAndEnterprise(id, idEnterprise);
        
        // Solo validar que la cuenta no está asociada a impuestos
        validationService.validateAccountNotAssociatedWithTaxes(accountToDelete);
        
        // Proceder con el soft delete
        accountCatalogueDeleteOutputPort.deleteById(id);
    }
}
