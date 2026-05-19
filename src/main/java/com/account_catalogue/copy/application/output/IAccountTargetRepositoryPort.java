package com.account_catalogue.copy.application.output;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

/**
 * Puerto de salida para insertar cuentas en la empresa destino.
 * La implementación debe usar TenantContext para forzar entDestino.
 */
public interface IAccountTargetRepositoryPort {

    /**
     * Persiste una cuenta nueva en la empresa destino.
     * El caller es responsable de haber seteado TenantContext.setTenantId(entDestino) antes.
     *
     * @param entity cuenta a insertar (sin id — se genera por IDENTITY)
     * @return entidad con el id generado
     */
    AccountCatalogueEntity guardar(AccountCatalogueEntity entity);
}
