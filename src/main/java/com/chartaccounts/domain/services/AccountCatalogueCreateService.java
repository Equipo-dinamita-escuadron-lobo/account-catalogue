package com.chartaccounts.domain.services;

import com.chartaccounts.application.output.IAccountCatalogueCreateOutputPort;
import com.chartaccounts.domain.models.AccountCatalogue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chartaccounts.application.input.IAccountCatalogueCreateInputPort;


@Service
@AllArgsConstructor
public class AccountCatalogueCreateService implements IAccountCatalogueCreateInputPort {


    private  final IAccountCatalogueCreateOutputPort accountCatalogueCreateOutputPort;


    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {
        return accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);
    }
}
