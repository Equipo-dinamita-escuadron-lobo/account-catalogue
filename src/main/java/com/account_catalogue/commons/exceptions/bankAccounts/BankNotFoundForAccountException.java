package com.account_catalogue.commons.exceptions.bankAccounts;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class BankNotFoundForAccountException extends BaseBusinessException {
    
    public BankNotFoundForAccountException() {
        super(BankAccountErrorCode.BANK_NOT_FOUND_FOR_ACCOUNT);
    }
    
    public BankNotFoundForAccountException(String message) {
        super(BankAccountErrorCode.BANK_NOT_FOUND_FOR_ACCOUNT, message);
    }
}
