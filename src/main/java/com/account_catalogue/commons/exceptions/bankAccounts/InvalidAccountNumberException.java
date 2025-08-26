package com.account_catalogue.commons.exceptions.bankAccounts;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidAccountNumberException extends BaseBusinessException {
    
    public InvalidAccountNumberException() {
        super(BankAccountErrorCode.INVALID_ACCOUNT_NUMBER);
    }
    
    public InvalidAccountNumberException(String message) {
        super(BankAccountErrorCode.INVALID_ACCOUNT_NUMBER, message);
    }
}
