package com.account_catalogue.copy.infraestructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de response para la ejecución de una fase de copia.
 * Contrato uniforme — REQ-CONTRACT-03.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyPhaseResponseDto {

    /** COMPLETADO | COMPLETADO_CON_ADVERTENCIAS | ERROR_REINTENTABLE | ERROR_NO_REINTENTABLE */
    private String estado;

    private int registrosProcesados;

    private List<CopyEquivalenciaDto> equivalenciasGeneradas;

    private String mensaje;

    private List<String> advertencias;

    /** Datos exportados en modo BACKUP. Null en DUPLICATE y RESTORE. */
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private Object datosExportados;
}
