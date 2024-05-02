package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountCatalogueSearchService implements IAccountCatalogueSearchInputPort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;

    @Override
    public List<AccountCatalogueInfoDTO> getAllAccountCatalogue(String code) {
        return accountCatalogueSearchOutputPort.getAllAccountCatalogue(code);
    }

    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code);
    }

    @Override
    public AccountCatalogue getAccountCatalogueTree(String code) {
        return accountCatalogueSearchOutputPort.getAccountCatalogueTree(code);
    }
    
}
