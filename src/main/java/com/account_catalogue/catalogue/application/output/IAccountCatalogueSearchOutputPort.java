package com.account_catalogue.catalogue.application.output;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchOutputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id);

    AccountCatalogue getAccountCatalogueByIdAndIdEnterprise(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByIdAndIdEnterprise(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueByDescriptionAndIdEnterprise(String description, String idEnterprise);
}
