package com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxChangeStateRes {
    private Long id;
    private String code;
    private String description;
    private Boolean status;
    private String message;
}
