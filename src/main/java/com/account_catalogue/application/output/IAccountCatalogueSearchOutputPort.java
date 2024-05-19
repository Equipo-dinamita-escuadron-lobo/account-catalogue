package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchOutputPort {

    AccountCatalogue getAccountCatalogueByCode(String code);

    AccountCatalogue getAccountCatalogueTree(String code);
}
