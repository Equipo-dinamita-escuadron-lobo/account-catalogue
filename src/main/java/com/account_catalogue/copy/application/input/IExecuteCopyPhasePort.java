package com.account_catalogue.copy.application.input;

import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyPhaseResponseDto;

/**
 * Puerto de entrada para ejecutar una fase del proceso de copia.
 * REQ-CONTRACT-01, REQ-PARTICIPANT-02, REQ-PARTICIPANT-03.
 */
public interface IExecuteCopyPhasePort {

    /**
     * Ejecuta la fase indicada en el request, copiando Account y Tax con
     * soporte de idempotencia, topological sort y tenant override.
     *
     * @param request datos de la fase a ejecutar
     * @return resultado de la ejecución con equivalencias generadas
     */
    CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request);
}
