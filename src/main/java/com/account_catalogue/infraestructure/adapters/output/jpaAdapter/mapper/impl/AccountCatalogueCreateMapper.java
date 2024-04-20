package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.impl;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AccountCatalogueCreateMapper implements IAccountCatalogueCreateMapper {
    @Override
    public AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue) {
        AccountCatalogueEntity accountCatalogueEntity=AccountCatalogueEntity.builder()
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature())
                .financialStatus(accountCatalogue.getFinancialStatus())
                .classification(accountCatalogue.getClassification())
                .build();

        return accountCatalogueEntity;
    }

    @Override
    public AccountCatalogue toModel(AccountCatalogueEntity accountCatalogueEntity) {
        AccountCatalogue accountCatalogue=AccountCatalogue.builder()
                .id(accountCatalogueEntity.getId())
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .nature(accountCatalogueEntity.getNature())
                .financialStatus(accountCatalogueEntity.getFinancialStatus())
                .classification(accountCatalogueEntity.getClassification())
                .build();

        return accountCatalogue;
    }
}
