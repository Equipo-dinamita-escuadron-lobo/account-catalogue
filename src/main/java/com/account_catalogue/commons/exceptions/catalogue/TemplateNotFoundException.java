package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando no se encuentra la plantilla del catálogo de cuentas.
 */
public class TemplateNotFoundException extends BaseBusinessException {

    public TemplateNotFoundException() {
        super(AccountCatalogueErrorCode.TEMPLATE_NOT_FOUND);
    }

    public TemplateNotFoundException(String customMessage) {
        super(AccountCatalogueErrorCode.TEMPLATE_NOT_FOUND, customMessage);
    }

    public TemplateNotFoundException(String customMessage, Throwable cause) {
        super(AccountCatalogueErrorCode.TEMPLATE_NOT_FOUND, customMessage, cause);
    }
}
