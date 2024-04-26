package com.account_catalogue.application.input;

import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;

import java.util.List;

public interface IAccountCatalogueSearchInputPort {
    List<AccountCatalogueInfoDTO> getAllAccountCatalogue(String code);
    AccountCatalogue getAccountCatalogueByCode(String code);

}
