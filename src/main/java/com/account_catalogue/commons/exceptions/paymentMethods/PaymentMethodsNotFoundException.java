package com.account_catalogue.commons.exceptions.paymentMethods;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class PaymentMethodsNotFoundException extends BaseBusinessException {

    public PaymentMethodsNotFoundException() {
        super(PaymentMethodsErrorCode.PAYMENT_METHOD_NOT_FOUND);
    }
}
