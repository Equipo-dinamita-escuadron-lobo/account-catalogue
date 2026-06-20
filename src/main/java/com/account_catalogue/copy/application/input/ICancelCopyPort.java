package com.account_catalogue.copy.application.input;

import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyCancelResponseDto;

/**
 * Puerto de entrada para cancelar un proceso de copia en curso.
 */
public interface ICancelCopyPort {

    /**
     * Cancela el proceso de copia indicado.
     *
     * @param idProceso UUID del proceso
     * @return respuesta con estado resultante
     */
    CopyCancelResponseDto cancelar(String idProceso);
}
