package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Repositorio JPA para leer cuentas de la empresa origen filtradas por snapshot.
 */
public interface AccountCopySourceRepository extends JpaRepository<AccountCatalogueEntity, Long> {

    /**
     * Retorna todas las cuentas de una empresa cuyo created_at es <= snapshotCorte.
     * Carga el parent para facilitar el topological sort sin lazy loading.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a LEFT JOIN FETCH a.parent " +
           "WHERE a.idEnterprise = :entOrigen AND a.createdAt <= :snapshotCorte")
    List<AccountCatalogueEntity> findByEntOrigenBeforeSnapshot(
            @Param("entOrigen") String entOrigen,
            @Param("snapshotCorte") Instant snapshotCorte);
}
