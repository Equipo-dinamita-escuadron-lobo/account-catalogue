package com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response;

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
    private Long refundAccountId;
    private Long depositAccountId;
    private Boolean status;
}
