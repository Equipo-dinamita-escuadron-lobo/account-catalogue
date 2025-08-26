package com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxCreateReq {
    @JsonIgnore
    private  Long id;
    @NotBlank(message = "El id de la empresa es requerido")
    private String idEnterprise;
    @NotBlank(message = "El código es requerido")
    private String code;
    @NotBlank(message = "la descripcion es requerdio")
    private String description;
    @NotBlank(message = "la descripcion es requerdio")
    private float interest;
    private String refundAccount;
    private String depositAccount;
}
