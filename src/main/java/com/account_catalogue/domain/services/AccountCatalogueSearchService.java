package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AccountCatalogueSearchService implements IAccountCatalogueSearchInputPort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;

    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code, idEnterprise);
    }

    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueTree(code, idEnterprise);
    }
    
}
