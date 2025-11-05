package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemAccountCatalogueSearchRes {
    Long id;
    String code;
    private  String description;
    private String nature;
    private String financialStatus;
    private String classification;
    private String parent;
    private Boolean crossing;
    private Boolean costCenter;
    private Boolean status;
}
