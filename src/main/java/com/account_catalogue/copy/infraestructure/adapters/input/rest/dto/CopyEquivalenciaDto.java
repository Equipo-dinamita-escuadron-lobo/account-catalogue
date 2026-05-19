package com.account_catalogue.copy.infraestructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de equivalencia de entidad: tabla + idViejo → idNuevo.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyEquivalenciaDto {

    private String modulo;
    private String tabla;
    private String idViejo;
    private String idNuevo;
}
