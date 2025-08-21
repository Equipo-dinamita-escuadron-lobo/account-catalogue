package com.account_catalogue.catalogue.application.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.services.AccountCatalogueValidationService;

@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Elimina un catálogo de cuenta por su ID.
     * Permite eliminación en cascada de cuentas hijas, pero valida que no esté asociada a impuestos.
     * 
     * @param id ID del catálogo de cuenta a eliminar
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        // Validar que la cuenta existe
        AccountCatalogue accountToDelete = validationService.validateAccountExistsById(id);
        
        // Solo validar que la cuenta no está asociada a impuestos
        // (Las cuentas hijas se eliminarán en cascada automáticamente)
        validationService.validateAccountNotAssociatedWithTaxes(accountToDelete);
        
        // Proceder con la eliminación (cascada automática de hijos)
        accountCatalogueDeleteOutputPort.deleteById(id);
    }
}
