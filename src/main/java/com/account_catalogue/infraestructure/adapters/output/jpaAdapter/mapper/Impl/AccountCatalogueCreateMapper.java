package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;

@Component
public class AccountCatalogueCreateMapper implements IAccountCatalogueCreateMapper{

    @Override
    public AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue, AccountCatalogueEntity parent) {

       if(accountCatalogue == null){
           return null;
       }

        AccountCatalogueEntity accountCatalogueEntity = AccountCatalogueEntity.builder()
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature())
                .financialStatus(accountCatalogue.getFinancialStatus())
                .classification(accountCatalogue.getClassification())
                .parent(parent)
                .build();
        
        parent = accountCatalogueEntity;       

        List<AccountCatalogueEntity> children = new ArrayList<>();
        for(AccountCatalogue child: accountCatalogue.getChildren()){
            AccountCatalogueEntity childAccountCatalogue = toEntity(child, parent);
            if(childAccountCatalogue != null){
                children.add(childAccountCatalogue);
            }
        }
        accountCatalogueEntity.setChildren(children);

        return accountCatalogueEntity;
    }

    @Override
    public AccountCatalogue toModel(AccountCatalogueEntity accountCatalogueEntity) {
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

        List<AccountCatalogue> children = new ArrayList<>();
        for(AccountCatalogueEntity child: accountCatalogueEntity.getChildren()){
            AccountCatalogue childAccountCatalogue = toModel(child);
            if(childAccountCatalogue != null){
                children.add(childAccountCatalogue);
            }
        }
        accountCatalogue.setChildren(children);

        return accountCatalogue;
    }
    


    
}
