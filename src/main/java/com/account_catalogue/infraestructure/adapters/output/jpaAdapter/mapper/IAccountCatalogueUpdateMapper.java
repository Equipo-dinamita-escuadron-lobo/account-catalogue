package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IAccountCatalogueUpdateMapper {
    default AccountCatalogue toAccountCatalogue(AccountCatalogueEntity accountCatalogueEntity){
        if(accountCatalogueEntity==null){
            return null;
        }

        return AccountCatalogue.builder()
                .id(accountCatalogueEntity.getId())
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .nature(accountCatalogueEntity.getNature())
                .financialStatus(accountCatalogueEntity.getFinancialStatus())
                .classification(accountCatalogueEntity.getClassification())
                .build();
    }

    default AccountCatalogueEntity toAccountCatalogueEntity(AccountCatalogue accountCatalogue){
        if (accountCatalogue==null){
            return null;
        }
        return AccountCatalogueEntity.builder()
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature())
                .financialStatus(accountCatalogue.getFinancialStatus())
                .classification(accountCatalogue.getClassification())
                .build();
    }
}
