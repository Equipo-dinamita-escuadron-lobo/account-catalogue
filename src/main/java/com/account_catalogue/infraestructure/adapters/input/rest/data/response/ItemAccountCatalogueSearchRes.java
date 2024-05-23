package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemAccountCatalogueSearchRes {
    Long id;
    String code;
    private  String description;
    private String  nature;
    private String  financialStatus;
    private String classification;
}
