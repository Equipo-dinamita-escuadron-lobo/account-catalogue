package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class AccountCatalogueAlreadyExistsException extends BaseBusinessException {
    
    public AccountCatalogueAlreadyExistsException() {
        super(AccountCatalogueErrorCode.ACCOUNT_ALREADY_EXISTS);
    }
    
    public AccountCatalogueAlreadyExistsException(String customMessage) {
        super(AccountCatalogueErrorCode.ACCOUNT_ALREADY_EXISTS, customMessage);
    }
    
    public AccountCatalogueAlreadyExistsException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.ACCOUNT_ALREADY_EXISTS, customMessage, cause);
    }
}
