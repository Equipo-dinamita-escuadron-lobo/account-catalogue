package com.account_catalogue.application.input;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueCreateInputPort {
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
