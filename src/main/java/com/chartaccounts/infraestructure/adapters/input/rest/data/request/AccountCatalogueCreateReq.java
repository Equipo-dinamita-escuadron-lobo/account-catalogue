package com.chartaccounts.infraestructure.adapters.input.rest.data.request;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class AccountCatalogueCreateReq {
    //El id no es necesario ya que es autogenerado
    @JsonIgnore
    private long id;
    @NotBlank(message = "El código es requerido")
    private String code;
    @NotBlank(message = "La descripcion es requerida")
    private String description;
    @NotBlank(message = "La naturaleza es requerida")
    private String nature;
    @NotBlank(message = "La estado financiero es requerido")
    private String financialStatus;
    @NotBlank(message = "La clasificacion es requerido")
    private String classification;
}
