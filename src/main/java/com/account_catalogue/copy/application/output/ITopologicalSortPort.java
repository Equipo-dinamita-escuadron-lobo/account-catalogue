package com.account_catalogue.copy.application.output;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import java.util.List;

/**
 * Puerto de salida para ordenar cuentas en orden topológico (padre antes que hijo).
 * Detecta ciclos y los reporta como excepción.
 */
public interface ITopologicalSortPort {

    /**
     * Ordena las cuentas para que cada padre aparezca antes que sus hijos.
     *
     * @param cuentas lista plana de cuentas de entOrigen
     * @return lista ordenada topológicamente
     * @throws com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException
     *         si se detecta un ciclo en la jerarquía
     */
    List<AccountCatalogueEntity> ordenar(List<AccountCatalogueEntity> cuentas);
}
