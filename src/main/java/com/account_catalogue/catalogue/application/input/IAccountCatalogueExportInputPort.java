package com.account_catalogue.catalogue.application.input;

import org.springframework.core.io.Resource;

/**
 * @brief Puerto de entrada para operaciones de exportación de catálogo de cuentas
 *
 * Define el contrato para exportar plantillas y datos del catálogo de cuentas
 * a formatos Excel con validaciones incluidas.
 */
public interface IAccountCatalogueExportInputPort {

    /**
     * @brief Exporta plantilla de catálogo de cuentas con validaciones
     * @param entId ID de la empresa
     * @return archivo Excel con estructura y validaciones para importación
     */
    Resource exportAccountCatalogueTemplate(String entId);

    /**
     * @brief Exporta catálogo de cuentas existente con validaciones
     * @param entId ID de la empresa
     * @param status filtro por estado (true=activos, false=inactivos, null=todos)
     * @return archivo Excel con datos actuales y validaciones
     */
    Resource exportAccountCatalogueWithValidations(String entId, Boolean status);
}