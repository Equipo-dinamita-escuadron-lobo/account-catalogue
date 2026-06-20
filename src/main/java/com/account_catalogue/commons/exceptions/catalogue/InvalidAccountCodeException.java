package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class InvalidAccountCodeException extends BaseBusinessException {
    
    public InvalidAccountCodeException() {
        super(AccountCatalogueErrorCode.INVALID_ACCOUNT_CODE_LENGTH);
    }
    
    public InvalidAccountCodeException(String customMessage) {
        super(AccountCatalogueErrorCode.INVALID_ACCOUNT_CODE_LENGTH, customMessage);
    }
    
    public InvalidAccountCodeException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.INVALID_ACCOUNT_CODE_LENGTH, customMessage, cause);
    }
}
