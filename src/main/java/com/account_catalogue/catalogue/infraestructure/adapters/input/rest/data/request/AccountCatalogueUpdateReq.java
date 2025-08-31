package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueUpdateReq {

    private String code;
    private String description;
    private String nature;
    private String financialStatus;
    private String classification;
    private Boolean crossing;
    private Boolean costCenter;
    private Boolean status;
}
