package com.account_catalogue.catalogue.application.input;

import org.springframework.core.io.Resource;

/**
 * Puerto de entrada para la exportación de plantilla del catálogo de cuentas.
 */
public interface IAccountCatalogueExportTemplateInputPort {
    
    /**
     * Obtiene la plantilla de catálogo de cuentas para descarga.
     * 
     * @return Resource con la plantilla de Excel
     */
    Resource getAccountCatalogueTemplate();
}
