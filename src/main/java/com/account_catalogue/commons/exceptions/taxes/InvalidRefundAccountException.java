package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidRefundAccountException extends BaseBusinessException {

    public InvalidRefundAccountException() {
        super(TaxesErrorCode.INVALID_REFUND_ACCOUNT);
    }

    public InvalidRefundAccountException(String message) {
        super(TaxesErrorCode.INVALID_REFUND_ACCOUNT, message);
    }

    public InvalidRefundAccountException(String message, Throwable cause) {
        super(TaxesErrorCode.INVALID_REFUND_ACCOUNT, message, cause);
    }
}
