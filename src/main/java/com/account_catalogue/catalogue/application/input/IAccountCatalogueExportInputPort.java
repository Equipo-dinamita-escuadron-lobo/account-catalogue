package com.account_catalogue.catalogue.application.input;

import org.springframework.core.io.Resource;

/**
 * Puerto de entrada para la exportación del catálogo de cuentas
 */
public interface IAccountCatalogueExportInputPort {

    /**
     * Exporta una plantilla de catálogo de cuentas con validaciones de datos.
     *
     * @param entId ID de la entidad
     * @return Resource que contiene la plantilla Excel con validaciones
     */
    Resource exportAccountCatalogueTemplate(String entId);

    /**
     * Exporta el catálogo de cuentas existente con validaciones de datos.
     *
     * @param entId ID de la entidad
     * @param status Estado de filtrado (true=activos, false=inactivos, null para todos)
     * @return Resource que contiene el archivo Excel con datos y validaciones
     */
    Resource exportAccountCatalogueWithValidations(String entId, Boolean status);
}