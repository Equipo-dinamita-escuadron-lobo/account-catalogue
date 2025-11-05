package com.account_catalogue.taxes.infraestructure.adapters.input.rest.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxCreateRes {

    private  Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private double interest;
    private Long purchaseTaxId;
    private Long salesTaxId;
    private Boolean status;
}
