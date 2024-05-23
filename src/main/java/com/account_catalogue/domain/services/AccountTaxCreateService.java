package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.IAccountTaxCreateInputPort;
import com.account_catalogue.application.output.IAccountTaxCreateOutputPort;
import com.account_catalogue.domain.models.AccountTax;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountTaxCreateService implements IAccountTaxCreateInputPort {

   // private final IAccountTaxCreateOutputPort accountTaxCreateOutputPort;
    @Override
    public AccountTax createAccounTax(AccountTax accountTax) {
        return null;
    }
}
