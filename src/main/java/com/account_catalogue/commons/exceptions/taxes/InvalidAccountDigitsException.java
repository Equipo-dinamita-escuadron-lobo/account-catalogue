package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidAccountDigitsException extends BaseBusinessException {

    public InvalidAccountDigitsException(String message) {
        super(TaxesErrorCode.INVALID_ACCOUNT_DIGITS, message);
    }

    public InvalidAccountDigitsException(String message, Throwable cause) {
        super(TaxesErrorCode.INVALID_ACCOUNT_DIGITS, message, cause);
    }
}
