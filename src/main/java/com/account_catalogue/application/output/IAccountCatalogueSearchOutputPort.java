package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchOutputPort {

    AccountCatalogue getAccountCatalogueByCode(String code,String idEnterprise);

    AccountCatalogue getAccountCatalogueTree(String code,String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id);
}
