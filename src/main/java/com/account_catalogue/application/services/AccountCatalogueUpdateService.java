package com.account_catalogue.application.services;

import com.account_catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueUpdateService implements IAccountCatalogueUpdateInputPort {

    private final IAccountCatalogueUpdateOutputPort accountCatalogueUpdateOutputport;

    /**
     * Actualiza un catalogo de cuenta.
     *
     * @param id El id del catalogo de cuenta a actualizar.
     * @param accountCatalogue El cat logo de cuenta actualizado.
     * @return El catalogo de cuenta actualizado.
     */
    
    @Override
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {
        return accountCatalogueUpdateOutputport.updateAccountCatalogue(id, accountCatalogue);
    }
}
