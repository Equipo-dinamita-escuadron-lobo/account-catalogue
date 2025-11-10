package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class AccountCatalogueAssociatedWithAccountingMovementsException extends BaseBusinessException {

    public AccountCatalogueAssociatedWithAccountingMovementsException() {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_ACCOUNTING_MOVEMENTS);
    }

    public AccountCatalogueAssociatedWithAccountingMovementsException(String customMessage) {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_ACCOUNTING_MOVEMENTS, customMessage);
    }

    public AccountCatalogueAssociatedWithAccountingMovementsException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_ACCOUNTING_MOVEMENTS, customMessage, cause);
    }
}
