package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class TaxNotFoundException extends BaseBusinessException {

    public TaxNotFoundException(String message) {
        super(TaxesErrorCode.TAX_NOT_FOUND, message);
    }

    public TaxNotFoundException(String message, Throwable cause) {
        super(TaxesErrorCode.TAX_NOT_FOUND, message, cause);
    }
}
