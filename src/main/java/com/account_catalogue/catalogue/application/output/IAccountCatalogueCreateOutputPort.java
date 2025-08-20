package com.account_catalogue.catalogue.application.output;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueCreateOutputPort {
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
