package com.account_catalogue.domain.DTO;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TaxDTO {

    private  Long id;
    private String code;
    private String description;
    private float interest;
    private String depositAccount;
    private  String refundAccount;
}
