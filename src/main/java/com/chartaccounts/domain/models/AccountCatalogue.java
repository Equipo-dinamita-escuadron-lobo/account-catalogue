package com.chartaccounts.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCatalogue {
    private long id;
    private String code;
    private  String description;
    private String nature;
    private String financialStatus;
    private String classification;

}
