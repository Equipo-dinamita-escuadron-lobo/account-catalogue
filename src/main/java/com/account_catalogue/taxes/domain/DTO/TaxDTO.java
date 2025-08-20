package com.account_catalogue.taxes.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TaxDTO {


    private  Long id;
    private  String idEnterprise;
    private String code;
    private String description;
    private float interest;
    private String depositAccount;
    private  String refundAccount;
}
