package com.account_catalogue.copy.domain.models;

import com.account_catalogue.copy.domain.enums.CopyJobState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Modelo de dominio que representa el registro de un trabajo de copia.
 * Sirve como registro de idempotencia y auditoría de cada ejecución de fase.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyJobLog {

    /** Identificador del proceso de copia (generado por el orquestador). */
    private UUID idProceso;

    /** Número de fase ejecutada. */
    private Integer fase;

    /** Nombre del módulo participante. */
    private String modulo;

    /** Estado actual del trabajo. */
    private CopyJobState estado;

    /** Momento de inicio de la ejecución. */
    private Instant fechaInicio;

    /** Momento de fin de la ejecución (null si aún no terminó). */
    private Instant fechaFin;

    /** Cantidad de equivalencias generadas en esta fase. */
    private Integer equivalenciasGeneradas;

    /** Mensaje de error en caso de fallo. */
    private String errorMessage;
}
