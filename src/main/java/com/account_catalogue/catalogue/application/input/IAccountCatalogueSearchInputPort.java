package com.account_catalogue.catalogue.application.input;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchInputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise);

    List<AccountCatalogue> getAccountCatalogueTrees(String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccounts(String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsWithCrossing(String idEnterprise);

    Page<AccountCatalogue> getAllAccountCatalogues(String idEnterprise, Pageable pageable);


    List<AccountCatalogue> getAllAccountsByEnterprise(String idEnterprise);

    
    List<AccountCatalogue> getAccountsByCodeOrDescription(String idEnterprise, String search);
}
