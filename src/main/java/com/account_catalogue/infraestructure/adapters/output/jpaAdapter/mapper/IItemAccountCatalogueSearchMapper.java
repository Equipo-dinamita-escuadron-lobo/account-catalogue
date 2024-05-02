package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

@Mapper
public interface IItemAccountCatalogueSearchMapper {

   default AccountCatalogue toDomain(AccountCatalogueEntity accountCatalogueEntity){
       if(accountCatalogueEntity==null ){
           return null;
       }
       return AccountCatalogue.builder()
               .code(accountCatalogueEntity.getCode())
               .description(accountCatalogueEntity.getDescription())
               .nature(accountCatalogueEntity.getNature())
               .financialStatus(accountCatalogueEntity.getFinancialStatus())
               .classification(accountCatalogueEntity.getClassification())
               .build();
   }
    AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue);

   default AccountCatalogue toDomainTree(AccountCatalogueEntity accountCatalogueEntity){
        if (accountCatalogueEntity == null) {
            return null;
        }

        AccountCatalogue accountCatalogue = AccountCatalogue.builder()
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .build();

        List<AccountCatalogue> children = new ArrayList<>();
        for (AccountCatalogueEntity child : accountCatalogueEntity.getChildren()) {
            AccountCatalogue childAccountCatalogue = toDomainTree(child);
            if (childAccountCatalogue != null) {
                children.add(childAccountCatalogue);
            }
        }
        accountCatalogue.setChildren(children);

        return accountCatalogue;
   }


}


