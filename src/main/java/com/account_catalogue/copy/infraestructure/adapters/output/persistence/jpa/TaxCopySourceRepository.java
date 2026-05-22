package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Repositorio JPA para leer impuestos de la empresa origen filtrados por snapshot.
 */
public interface TaxCopySourceRepository extends JpaRepository<TaxEntity, Long> {

    /**
     * Retorna todos los impuestos de una empresa cuyo created_at es <= snapshotCorte.
     * Carga salesTax y purchaseTax para facilitar el remapeo sin lazy loading.
     */
    @Query("SELECT t FROM TaxEntity t " +
           "LEFT JOIN FETCH t.salesTax " +
           "LEFT JOIN FETCH t.purchaseTax " +
           "WHERE t.idEnterprise = :entOrigen AND (t.createdAt IS NULL OR t.createdAt <= :snapshotCorte)")
    List<TaxEntity> findByEntOrigenBeforeSnapshot(
            @Param("entOrigen") String entOrigen,
            @Param("snapshotCorte") Instant snapshotCorte);
}
