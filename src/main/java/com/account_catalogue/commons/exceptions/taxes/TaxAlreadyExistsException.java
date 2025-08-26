package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class TaxAlreadyExistsException extends BaseBusinessException {

    public TaxAlreadyExistsException(String message) {
        super(TaxesErrorCode.TAX_ALREADY_EXISTS, message);
    }

    public TaxAlreadyExistsException(String message, Throwable cause) {
        super(TaxesErrorCode.TAX_ALREADY_EXISTS, message, cause);
    }
}
