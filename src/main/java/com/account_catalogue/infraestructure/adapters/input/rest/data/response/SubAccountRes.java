package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubAccountRes {
    private long id;
    private String code;
    private String description;
    private AssistantRes assistant1;
}
