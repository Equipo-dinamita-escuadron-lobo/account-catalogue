package com.account_catalogue.copy.application.output;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer impuestos de la empresa origen.
 */
public interface ITaxSourceRepositoryPort {

    /**
     * Retorna todos los impuestos de la empresa origen creados antes del snapshot.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte límite superior de created_at
     * @return lista de impuestos a copiar
     */
    List<TaxEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte);
}
