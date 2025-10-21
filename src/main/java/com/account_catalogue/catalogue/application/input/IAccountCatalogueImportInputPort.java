package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;

/**
 * Puerto de entrada para la funcionalidad de importación de catálogo de cuentas.
 * Define el contrato para importar cuentas contables desde archivos Excel.
 */
public interface IAccountCatalogueImportInputPort {

    /**
     * Importa cuentas contables desde un archivo Excel.
     * 
     * @param request solicitud de importación conteniendo archivo y metadatos
     * @return respuesta con resultados de la importación
     */
    AccountCatalogueImportResponse importAccountCatalogueFromExcel(AccountCatalogueImportRequest request);
}

