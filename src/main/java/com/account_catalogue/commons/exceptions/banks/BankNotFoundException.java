package com.account_catalogue.commons.exceptions.banks;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class BankNotFoundException extends BaseBusinessException {
    
    public BankNotFoundException() {
        super(BankErrorCode.BANK_NOT_FOUND);
    }
    
    public BankNotFoundException(String message) {
        super(BankErrorCode.BANK_NOT_FOUND, message);
    }
}
