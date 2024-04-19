package com.chartaccounts.application.input;

import com.chartaccounts.domain.models.AccountCatalogue;

public interface IAccountCatalogueCreateInputPort {
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
