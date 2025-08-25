package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueCreateRes {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private String nature;
    private String financialStatus;
    private String classification;
    private String parent;
    private Boolean crossing;
    private Boolean costCenter;

}
