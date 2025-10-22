package com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxUpdateReq {
    @JsonIgnore
    private  Long id;
    @NotBlank(message = "El id de la empresa es requerido")
    private String idEnterprise;
    @NotBlank(message = "El código es requerido")
    private String code;
    @NotBlank(message = "La descripción es requerida")
    private String description;
    @NotNull(message = "El interés es requerido")
    private Double interest;
    @NotNull(message = "La cuenta de impuesto de compra es requerida")
    private Long purchaseTaxId;
    @NotNull(message = "La cuenta de impuesto de venta es requerida")
    private Long salesTaxId;
}
