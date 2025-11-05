package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueUpdateReq {

    private String idEnterprise; 
    private String code;
    private String description;
    private String nature;
    private String financialStatus;
    private String classification;
    private Long parent;
    private Boolean crossing;
    private Boolean costCenter;
    private Boolean status;
}
