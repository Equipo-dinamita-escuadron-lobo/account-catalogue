package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class AccountCatalogueAssociatedWithBankAccountException extends BaseBusinessException {

    public AccountCatalogueAssociatedWithBankAccountException() {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_BANK_ACCOUNT);
    }

    public AccountCatalogueAssociatedWithBankAccountException(String customMessage) {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_BANK_ACCOUNT, customMessage);
    }

    public AccountCatalogueAssociatedWithBankAccountException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_BANK_ACCOUNT, customMessage, cause);
    }
}