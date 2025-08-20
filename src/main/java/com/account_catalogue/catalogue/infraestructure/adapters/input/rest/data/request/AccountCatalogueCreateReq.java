package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class AccountCatalogueCreateReq {
    //El id no es necesario ya que es autogenerado
    @JsonIgnore
    private Long id;

    private String idEnterprise;

    @NotBlank(message = "El código es requerido")
    private String code;

    @NotBlank(message = "La descripcion es requerida")
    private String description;

   // @NotBlank(message = "La naturaleza es requerida")
    private String nature;

    //@NotBlank(message = "La estado financiero es requerido")
    private String financialStatus;
    
    //@NotBlank(message = "La clasificacion es requerido")
    private String classification;
    
    private Long parent;

    private List<AccountCatalogueCreateReq> children;
}
