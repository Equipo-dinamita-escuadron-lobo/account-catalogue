package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueUpdateRes {
    private long id;
    private String code;
    private String description;
    private String nature;
    private String financialStatus;
    private String classification;
    private String parent;
}
