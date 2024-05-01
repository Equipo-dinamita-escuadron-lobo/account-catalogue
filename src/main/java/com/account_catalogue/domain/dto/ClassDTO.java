package com.account_catalogue.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassDTO {
    private int id;
    private String code;
    private  String description;
    List<GrupDTO> grupo;
}
