package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueCreateOutputPort {
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
