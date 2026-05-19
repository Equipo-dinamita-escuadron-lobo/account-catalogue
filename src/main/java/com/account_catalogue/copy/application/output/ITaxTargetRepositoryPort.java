package com.account_catalogue.copy.application.output;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

/**
 * Puerto de salida para insertar impuestos en la empresa destino.
 */
public interface ITaxTargetRepositoryPort {

    /**
     * Persiste un impuesto nuevo en la empresa destino.
     *
     * @param entity impuesto a insertar (sin id — se genera por IDENTITY)
     * @return entidad con el id generado
     */
    TaxEntity guardar(TaxEntity entity);
}
