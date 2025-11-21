package com.account_catalogue.catalogue.application.input;

/**
 * @brief Puerto de entrada para gestión de uso de cuentas contables
 *
 * Define contrato para operaciones relacionadas con el contador de uso de cuentas contables,
 * utilizado cuando otros servicios notifican que han utilizado una cuenta contable.
 */
public interface IAccountCatalogueUsagePort {
    /**
     * @brief Incrementa el contador de uso de una cuenta contable
     * @param accountCatalogueId ID de la cuenta contable
     * @param enterpriseId ID de la empresa
     */
    void incrementUsageCount(Long accountCatalogueId, String enterpriseId);
}

