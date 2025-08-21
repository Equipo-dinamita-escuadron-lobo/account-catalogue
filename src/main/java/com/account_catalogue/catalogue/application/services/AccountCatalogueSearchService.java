package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.services.AccountCatalogueValidationService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueSearchService implements IAccountCatalogueSearchInputPort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Obtiene el catálogo de cuenta por código y ID de empresa con validación de existencia.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        return validationService.validateAccountExists(code, idEnterprise);
    }

    /**
     * Obtiene el árbol del catálogo de cuenta por código y ID de empresa con validación de existencia.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el árbol del catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        // Primero validar que existe
        validationService.validateAccountExists(code, idEnterprise);
        // Luego obtener el árbol
        return accountCatalogueSearchOutputPort.getAccountCatalogueTree(code, idEnterprise);
    }

    /**
     * Obtiene el catálogo de cuenta por ID con validación de existencia.
     *
     * @param id el ID del catálogo de cuenta
     * @return el catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueById(Long id) {
        return validationService.validateAccountExistsById(id);
    }

}
