package com.account_catalogue.copy.application.output;

import com.account_catalogue.copy.domain.models.CopyJobLog;

import java.util.Optional;

/**
 * Puerto de salida para persistir y consultar registros de trabajos de copia.
 */
public interface ICopyJobLogRepositoryPort {

    /**
     * Guarda o actualiza un registro de trabajo de copia.
     */
    CopyJobLog guardar(CopyJobLog log);

    /**
     * Busca el log más reciente para el proceso e idProceso dados.
     */
    Optional<CopyJobLog> buscarPorIdProcesoYFase(String idProceso, int fase);

    /**
     * Busca el log más reciente por idProceso (cualquier fase).
     */
    Optional<CopyJobLog> buscarPorIdProceso(String idProceso);

    /**
     * Elimina todos los registros de log de un proceso.
     */
    void eliminarPorIdProceso(String idProceso);
}
