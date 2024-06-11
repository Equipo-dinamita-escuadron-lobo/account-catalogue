package com.account_catalogue.infraestructure.adapters.input.rest.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

public class TaxUpdateReq {
    @JsonIgnore
    private  Long id;
    @NotBlank(message = "El código es requerido")
    private String code;
    @NotBlank(message = "la descripcion es requerdio")
    private String description;
    @NotBlank(message = "la descripcion es requerdio")
    private float interest;
    private String refundAccount;
    private String depositAccount;
}
