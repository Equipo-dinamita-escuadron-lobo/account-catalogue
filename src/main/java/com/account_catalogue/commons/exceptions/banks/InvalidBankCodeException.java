package com.account_catalogue.commons.exceptions.banks;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidBankCodeException extends BaseBusinessException {
    
    public InvalidBankCodeException() {
        super(BankErrorCode.INVALID_BANK_CODE);
    }
    
    public InvalidBankCodeException(String message) {
        super(BankErrorCode.INVALID_BANK_CODE, message);
    }
}
