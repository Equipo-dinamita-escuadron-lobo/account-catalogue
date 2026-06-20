package com.account_catalogue.commons.exceptions.paymentMethods;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class AccountingAccountImmutableException extends BaseBusinessException {
    
    public AccountingAccountImmutableException() {
        super(PaymentMethodsErrorCode.ACCOUNTING_ACCOUNT_IMMUTABLE);
    }
    
    public AccountingAccountImmutableException(String message) {
        super(PaymentMethodsErrorCode.ACCOUNTING_ACCOUNT_IMMUTABLE, message);
    }
}
