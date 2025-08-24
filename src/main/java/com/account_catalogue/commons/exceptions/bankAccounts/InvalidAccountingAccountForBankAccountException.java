package com.account_catalogue.commons.exceptions.bankAccounts;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidAccountingAccountForBankAccountException extends BaseBusinessException {
    
    public InvalidAccountingAccountForBankAccountException() {
        super(BankAccountErrorCode.INVALID_ACCOUNTING_ACCOUNT_FOR_BANK_ACCOUNT);
    }
    
    public InvalidAccountingAccountForBankAccountException(String message) {
        super(BankAccountErrorCode.INVALID_ACCOUNTING_ACCOUNT_FOR_BANK_ACCOUNT, message);
    }
}
