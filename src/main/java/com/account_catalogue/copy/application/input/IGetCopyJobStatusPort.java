package com.account_catalogue.copy.application.input;

import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyStatusResponseDto;

/**
 * Puerto de entrada para consultar el estado de un proceso de copia.
 */
public interface IGetCopyJobStatusPort {

    /**
     * Retorna el estado actual del proceso identificado por idProceso.
     *
     * @param idProceso UUID del proceso de copia
     * @return estado actual
     */
    CopyStatusResponseDto obtenerEstado(String idProceso);
}
