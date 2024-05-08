package com.account_catalogue.application.input;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchInputPort {

    AccountCatalogue getAccountCatalogueByCode(String code);

    AccountCatalogue getAccountCatalogueTree(String code);

}
