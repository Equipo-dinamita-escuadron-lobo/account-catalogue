package com.account_catalogue.catalogue.application.output;

import java.util.List;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchOutputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueByIdAndIdEnterprise(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByIdAndIdEnterprise(Long id, String idEnterprise);
    
    AccountCatalogue getAccountCatalogueByDescriptionIgnoreCaseAndIdEnterprise(String description, String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsByIdEnterprise(String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsWithCrossingByIdEnterprise(String idEnterprise);
}
