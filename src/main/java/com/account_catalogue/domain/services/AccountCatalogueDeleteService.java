package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private  final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
   @Transactional
    @Override
    public void deleteByCode(String code) {
        accountCatalogueDeleteOutputPort.deleteByCode(code);
    }
}
