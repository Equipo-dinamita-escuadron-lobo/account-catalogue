package com.account_catalogue.commons.exceptions.catalogue;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

/**
 * Excepción específica para errores durante la exportación de catálogo de cuentas.
 * Extiende BaseBusinessException para mantener consistencia con el manejo de errores del dominio.
 */
public class AccountCatalogueExportException extends BaseBusinessException {

    /**
     * Constructor con mensaje y código de error.
     *
     * @param errorCode código específico del error
     * @param message mensaje descriptivo del error
     */
    public AccountCatalogueExportException(AccountCatalogueErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con mensaje, código de error y causa.
     *
     * @param errorCode código específico del error
     * @param message mensaje descriptivo del error
     * @param cause causa raíz del error
     */
    public AccountCatalogueExportException(AccountCatalogueErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor de conveniencia para exportación sin datos.
     *
     * @return nueva instancia de AccountCatalogueExportException
     */
    public static AccountCatalogueExportException forNoData() {
        return new AccountCatalogueExportException(
            AccountCatalogueErrorCode.ACCOUNT_EXPORT_NO_DATA,
            "No hay cuentas contables disponibles para exportar"
        );
    }
}
