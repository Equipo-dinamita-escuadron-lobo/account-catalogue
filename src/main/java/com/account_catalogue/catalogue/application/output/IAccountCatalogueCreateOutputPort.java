package com.account_catalogue.catalogue.application.output;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

/**
 * @brief Puerto de salida para operaciones de creación de cuentas contables
 *
 * Define el contrato para persistir nuevas cuentas contables
 * en el repositorio de datos.
 */
public interface IAccountCatalogueCreateOutputPort {
    /**
     * @brief Crea nueva cuenta contable en base de datos
     * @param accountCatalogue cuenta a persistir
     * @return cuenta creada con ID generado
     */
    AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue);
}
