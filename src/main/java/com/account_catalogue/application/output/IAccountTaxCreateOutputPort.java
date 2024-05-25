package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.AccountTax;

public interface IAccountTaxCreateOutputPort {
    AccountTax createAccounTax(String codeAccount, String codeTax);
}
