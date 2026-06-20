package com.account_catalogue.copy.application.output;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer cuentas de la empresa origen.
 * La implementación debe filtrar por entOrigen y created_at <= snapshotCorte.
 */
public interface IAccountSourceRepositoryPort {

    /**
     * Retorna todas las cuentas de la empresa origen creadas antes del snapshot.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte límite superior de created_at
     * @return lista de cuentas a copiar
     */
    List<AccountCatalogueEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte);
}
