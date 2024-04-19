package com.chartaccounts.application.output;

import com.chartaccounts.domain.models.AccountCatalogue;

public interface IAccountCatalogueCreateOutputPort {
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
