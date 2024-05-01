package com.account_catalogue.domain.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AncillaryDTO {
    private int id;
    private String code;
    private  String description;

}
