package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.Impl;

import org.springframework.stereotype.Component;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;

@Component
public class AccountCatalogueUpdateMapper implements IAccountCatalogueUpdateMapper{


    @Override
    public AccountCatalogue toAccountCatalogue(AccountCatalogueEntity accountCatalogueEntity) {
        if(accountCatalogueEntity == null){
            return null;
        }

        AccountCatalogue accountCatalogue = AccountCatalogue.builder()
                .id(accountCatalogueEntity.getId())
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .nature(accountCatalogueEntity.getNature())
                .financialStatus(accountCatalogueEntity.getFinancialStatus())
                .classification(accountCatalogueEntity.getClassification())
                .build();
        
        return accountCatalogue;
    }

    @Override
    public AccountCatalogueEntity toAccountCatalogueEntity(AccountCatalogue accountCatalogue) {
        if(accountCatalogue == null){
            return null;
        }

        AccountCatalogueEntity accountCatalogueEntity = AccountCatalogueEntity.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature())
                .financialStatus(accountCatalogue.getFinancialStatus())
                .classification(accountCatalogue.getClassification())
                .build();
        
        return accountCatalogueEntity;
    }
    
}
