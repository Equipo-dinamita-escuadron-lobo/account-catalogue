package com.account_catalogue.catalogue.application.output;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

/**
 * @brief Puerto de salida para operaciones de actualización de cuentas contables
 *
 * Define el contrato para modificar cuentas contables existentes
 * en el repositorio de datos.
 */
public interface IAccountCatalogueUpdateOutputPort {
    /**
     * @brief Actualiza cuenta contable existente en base de datos
     * @param id ID de la cuenta a actualizar
     * @param accountCatalogue datos actualizados de la cuenta
     * @return cuenta actualizada
     */
    AccountCatalogue updateAccountCatalogue(long id,AccountCatalogue accountCatalogue);
}
