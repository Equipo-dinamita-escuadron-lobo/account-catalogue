package com.account_catalogue.commons.exceptions.bankAccounts;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class BankAccountNotFoundException extends BaseBusinessException {
    
    public BankAccountNotFoundException() {
        super(BankAccountErrorCode.BANK_ACCOUNT_NOT_FOUND);
    }
    
    public BankAccountNotFoundException(String message) {
        super(BankAccountErrorCode.BANK_ACCOUNT_NOT_FOUND, message);
    }
}
