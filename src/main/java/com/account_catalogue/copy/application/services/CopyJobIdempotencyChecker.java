package com.account_catalogue.copy.application.services;

import com.account_catalogue.copy.application.output.ICopyJobLogRepositoryPort;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Verifica idempotencia de fase: si el mismo idProceso + fase ya fue ejecutado,
 * retorna el resultado previo sin re-ejecutar la copia.
 * REQ-PARTICIPANT-06.
 */
@Component
@RequiredArgsConstructor
public class CopyJobIdempotencyChecker {

    private final ICopyJobLogRepositoryPort repositoryPort;

    /**
     * Busca una ejecución previa completada (o en estado terminal) para el proceso y fase dados.
     *
     * @param idProceso UUID del proceso (como String)
     * @param fase      número de fase
     * @return el log previo si existe, o empty si es la primera vez
     */
    public Optional<CopyJobLog> buscarEjecucionPrevia(String idProceso, int fase) {
        return repositoryPort.buscarPorIdProcesoYFase(idProceso, fase);
    }
}
