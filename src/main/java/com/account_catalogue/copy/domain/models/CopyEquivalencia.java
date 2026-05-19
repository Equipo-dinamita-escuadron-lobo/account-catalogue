package com.account_catalogue.copy.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la equivalencia entre un ID original y su ID nuevo tras la copia.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyEquivalencia {

    /** Nombre de la tabla o módulo. */
    private String tabla;

    /** ID original en la empresa origen. */
    private String idViejo;

    /** ID nuevo asignado en la empresa destino. */
    private String idNuevo;
}
