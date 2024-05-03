package com.account_catalogue.infraestructure.adapters.input.rest.data.request;


import java.util.List;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private long id;

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
    
    private String parent;

    private List<AccountCatalogueCreateReq> children;
}
