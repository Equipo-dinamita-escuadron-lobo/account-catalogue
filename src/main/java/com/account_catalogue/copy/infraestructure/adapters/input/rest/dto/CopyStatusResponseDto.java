package com.account_catalogue.copy.infraestructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para consultar el estado de un proceso de copia.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyStatusResponseDto {

    private int fase;
    private String estado;
    private int registrosProcesados;
    private int intentos;
    private String ultimoError;
}
