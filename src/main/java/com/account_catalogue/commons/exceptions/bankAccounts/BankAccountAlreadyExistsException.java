package com.account_catalogue.commons.exceptions.bankAccounts;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class BankAccountAlreadyExistsException extends BaseBusinessException {

    public BankAccountAlreadyExistsException(String field, String value, String enterpriseId) {
        super(BankAccountErrorCode.BANK_ACCOUNT_ALREADY_EXISTS,
              String.format("Ya existe una cuenta bancaria con %s '%s'", field, value));
    }
}
