package com.account_catalogue.taxes.domain.models;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tax {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private Double interest;
    private AccountCatalogueEntity depositAccount;
    private AccountCatalogueEntity refundAccount;
    private Boolean status;
}
