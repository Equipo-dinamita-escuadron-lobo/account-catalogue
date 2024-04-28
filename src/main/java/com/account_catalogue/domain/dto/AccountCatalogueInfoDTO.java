package com.account_catalogue.domain.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueInfoDTO {
    int id;
    String code;
    String description;
}
