package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueUpdateInputPort {
    AccountCatalogue updateAccountCatalogue(long id,AccountCatalogue accountCatalogue);
}
