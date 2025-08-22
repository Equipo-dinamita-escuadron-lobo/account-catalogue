package com.account_catalogue.commons.exceptions.paymentMethods;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidAccountingAccountException extends BaseBusinessException {
    
    public InvalidAccountingAccountException() {
        super(PaymentMethodsErrorCode.INVALID_ACCOUNTING_ACCOUNT);
    }
    
    public InvalidAccountingAccountException(String customMessage) {
        super(PaymentMethodsErrorCode.INVALID_ACCOUNTING_ACCOUNT, customMessage);
    }
    
    public InvalidAccountingAccountException(String customMessage, Throwable cause) {
        super(PaymentMethodsErrorCode.INVALID_ACCOUNTING_ACCOUNT, customMessage, cause);
    }
}
