package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuxiliaryAccountListRes {
    private List<ItemAccountCatalogueSearchRes> auxiliaryAccounts;
    private int totalCount;
    private String idEnterprise;
}
