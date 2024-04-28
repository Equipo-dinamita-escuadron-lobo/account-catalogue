package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueSearchRes {
   int id;
    String code;
    String description;
}
