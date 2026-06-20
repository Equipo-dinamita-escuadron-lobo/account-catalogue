package com.account_catalogue.commons.exceptions.taxes;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class DuplicateTaxAccountsException extends BaseBusinessException {

    public DuplicateTaxAccountsException(String message) {
        super(TaxesErrorCode.DUPLICATE_TAX_ACCOUNTS, message);
    }

    public DuplicateTaxAccountsException(String message, Throwable cause) {
        super(TaxesErrorCode.DUPLICATE_TAX_ACCOUNTS, message, cause);
    }
}
