package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchInputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id);

}
