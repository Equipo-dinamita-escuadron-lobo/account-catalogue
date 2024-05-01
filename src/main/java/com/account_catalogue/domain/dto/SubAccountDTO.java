package com.account_catalogue.domain.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SubAccountDTO {
    private int id;
    private String code;
    private  String description;
    private List<AncillaryDTO> auxiliar;
}
