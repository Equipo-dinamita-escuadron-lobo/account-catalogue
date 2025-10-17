package com.account_catalogue.catalogue.application.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;

@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Realiza una eliminación de un catálogo de cuenta por su ID para una empresa específica.
     * Elimina la cuenta y todas sus cuentas hijas de forma física.
     * Valida recursivamente que ni la cuenta padre ni ninguna de sus cuentas hijas 
     * estén asociadas a impuestos antes de eliminarlas.
     * 
     * @param id ID del catálogo de cuenta a eliminar
     * @param idEnterprise ID de la empresa para la cual eliminar la cuenta
     */
    @Transactional
    @Override
    public void deleteById(Long id, String idEnterprise) {
        // Obtener el árbol completo de la cuenta con todas sus cuentas hijas
        AccountCatalogue accountTreeToDelete = accountCatalogueSearchOutputPort.getAccountCatalogueTreeByIdAndIdEnterprise(id, idEnterprise);
        
        // Validar que la cuenta existe
        if (accountTreeToDelete == null) {
            throw new AccountCatalogueNotFoundException(
                "No se encontró una cuenta con el ID '" + id + "'"
            );
        }
        
        // Validar recursivamente que ni la cuenta padre ni ninguna de sus hijas estén asociadas a impuestos
        validationService.validateAccountAndChildrenNotAssociatedWithTaxes(accountTreeToDelete);
        
        // Proceder con la eliminación recursiva
        accountCatalogueDeleteOutputPort.deleteById(id);
    }
}
