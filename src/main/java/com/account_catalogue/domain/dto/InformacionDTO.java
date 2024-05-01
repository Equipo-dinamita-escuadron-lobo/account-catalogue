package com.account_catalogue.domain.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformacionDTO {
    private int id;
    private String code;
    private  String description;
}
