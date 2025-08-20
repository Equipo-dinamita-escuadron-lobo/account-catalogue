package com.account_catalogue.taxes.domain.models;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Tax {
    private  Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private float interest;
    private AccountCatalogueEntity depositAccount;
    private  AccountCatalogueEntity refundAccount;
}
