package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;

/**
 * @brief Puerto de entrada para operaciones de importación de catálogo de cuentas
 *
 * Define el contrato para importar cuentas contables desde archivos Excel
 * con validaciones y procesamiento por lotes.
 */
public interface IAccountCatalogueImportInputPort {

    /**
     * @brief Importa cuentas contables desde archivo Excel
     * @param request solicitud con archivo Excel y configuración de importación
     * @return respuesta con resultados del proceso de importación
     */
    AccountCatalogueImportResponse importAccountCatalogueFromExcel(AccountCatalogueImportRequest request);
}

