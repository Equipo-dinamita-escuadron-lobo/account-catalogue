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
                .parent(auxParent(accountCatalogueEntity.getParent() == null ? null : accountCatalogueEntity.getParent()))
                .build();
        
        return accountCatalogue;
    }

    private AccountCatalogue auxParent(AccountCatalogueEntity accountCatalogue){
        if(accountCatalogue == null){
            return AccountCatalogue.builder()
                    .id(null)
                    .code(null)
                    .build();
        }

        return AccountCatalogue.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .build();
    }
    
}
