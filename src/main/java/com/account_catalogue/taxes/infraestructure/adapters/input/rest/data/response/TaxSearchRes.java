package com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaxSearchRes {
    private  Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private float interest;
    private String refundAccount;
    private String depositAccount;
    private Boolean status;
}
