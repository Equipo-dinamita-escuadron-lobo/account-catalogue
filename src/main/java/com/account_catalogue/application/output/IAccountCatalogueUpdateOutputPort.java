package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueUpdateOutputPort {
    AccountCatalogue updateAccountCatalogue(long id,AccountCatalogue accountCatalogue);
}
