package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidDepositAccountException extends BaseBusinessException {

    public InvalidDepositAccountException(String message) {
        super(TaxesErrorCode.INVALID_DEPOSIT_ACCOUNT, message);
    }

    public InvalidDepositAccountException(String message, Throwable cause) {
        super(TaxesErrorCode.INVALID_DEPOSIT_ACCOUNT, message, cause);
    }
}
