package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class AccountCatalogueInactiveException extends BaseBusinessException {

    public AccountCatalogueInactiveException() {
        super(AccountCatalogueErrorCode.ACCOUNT_INACTIVE);
    }

    public AccountCatalogueInactiveException(String customMessage) {
        super(AccountCatalogueErrorCode.ACCOUNT_INACTIVE, customMessage);
    }

    public AccountCatalogueInactiveException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.ACCOUNT_INACTIVE, customMessage, cause);
    }
}