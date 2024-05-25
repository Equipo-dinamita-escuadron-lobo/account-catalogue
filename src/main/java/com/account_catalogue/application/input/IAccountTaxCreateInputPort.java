package com.account_catalogue.application.input;

import com.account_catalogue.domain.models.AccountTax;

public interface IAccountTaxCreateInputPort {
  AccountTax createAccounTax(String codeAccount, String codeTax);
}
