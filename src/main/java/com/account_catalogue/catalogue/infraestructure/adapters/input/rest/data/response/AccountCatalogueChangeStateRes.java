package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueChangeStateRes {
    private Long id;
    private String code;
    private String description;
    private Boolean status;
    private String message;
}
