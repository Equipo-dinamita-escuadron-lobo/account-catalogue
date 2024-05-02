package com.account_catalogue.application.output;

import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;

import java.util.List;

public interface IAccountCatalogueSearchOutputPort {
    List<AccountCatalogueInfoDTO> getAllAccountCatalogue(String code);
    AccountCatalogue getAccountCatalogueByCode(String code);

    AccountCatalogue getAccountCatalogueTree(String code);
}
