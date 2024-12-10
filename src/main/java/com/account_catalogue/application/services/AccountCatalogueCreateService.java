package com.account_catalogue.application.services;

import com.account_catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;

@Service
@AllArgsConstructor
public class AccountCatalogueCreateService implements IAccountCatalogueCreateInputPort {

    private final IAccountCatalogueCreateOutputPort accountCatalogueCreateOutputPort;

    /**
     * Crea un catálogo de cuenta.
     *
     * @param accountCatalogue El catálogo de cuenta a crear.
     * @return El catálogo de cuenta creado.
     */
    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {
        return accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);
    }
}
