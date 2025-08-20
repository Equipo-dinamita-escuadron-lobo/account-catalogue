package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueSearchService implements IAccountCatalogueSearchInputPort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;

    /**
     * Obtiene el catálogo de cuenta por código y ID de empresa.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el catálogo de cuenta
     */
    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code, idEnterprise);
    }

    /**
     * Obtiene el árbol del catálogo de cuenta por código y ID de empresa.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el árbol del catálogo de cuenta, o null si no se encuentra
     */
    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueTree(code, idEnterprise);
    }

    /**
     * Obtiene el catálogo de cuenta por ID.
     *
     * @param id el ID del catálogo de cuenta
     * @return el catálogo de cuenta, o null si no se encuentra
     */
    @Override
    public AccountCatalogue getAccountCatalogueById(Long id) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueById(id);
    }

}
