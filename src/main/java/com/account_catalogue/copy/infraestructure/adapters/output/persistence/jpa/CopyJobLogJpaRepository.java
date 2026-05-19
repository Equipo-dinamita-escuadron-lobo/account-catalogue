package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad CopyJobLogEntity.
 */
public interface CopyJobLogJpaRepository extends JpaRepository<CopyJobLogEntity, Long> {

    /**
     * Busca el log del proceso para el par (idProceso, fase).
     */
    Optional<CopyJobLogEntity> findByIdProcesoAndFase(String idProceso, int fase);

    /**
     * Busca el log más reciente de un proceso (primer resultado por fecha desc).
     */
    Optional<CopyJobLogEntity> findTopByIdProcesoOrderByFechaInicioDesc(String idProceso);

    /**
     * Elimina todos los registros de log de un proceso.
     */
    void deleteByIdProceso(String idProceso);
}
