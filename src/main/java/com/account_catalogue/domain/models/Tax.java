package com.account_catalogue.domain.models;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;


import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Tax {
    private  Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private float interest;
    private AccountCatalogueEntity depositAccount;
    private  AccountCatalogueEntity refundAccount;


}
