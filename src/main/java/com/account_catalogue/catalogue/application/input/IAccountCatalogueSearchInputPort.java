package com.account_catalogue.catalogue.application.input;

import java.util.List;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchInputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccounts(String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsWithCrossing(String idEnterprise);

}
