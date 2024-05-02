package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IAccountCatalogueCreateMapper {
   default AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue){
       if(accountCatalogue==null){
           return null;
       }
       return AccountCatalogueEntity.builder()
                .id(accountCatalogue.getId())
               .code(accountCatalogue.getCode())
               .description(accountCatalogue.getDescription())
               .nature(accountCatalogue.getNature())
               .financialStatus(accountCatalogue.getFinancialStatus())
               .classification(accountCatalogue.getClassification())
               .parent(toEntity2(accountCatalogue.getParent()))
               .build();
   }

   default AccountCatalogueEntity toEntity2(AccountCatalogue accountCatalogue){
    if(accountCatalogue==null){
        return null;
    }
    return AccountCatalogueEntity.builder()
             .id(accountCatalogue.getId())
            .code(accountCatalogue.getCode())
            .description(accountCatalogue.getDescription())
            .nature(accountCatalogue.getNature())
            .financialStatus(accountCatalogue.getFinancialStatus())
            .classification(accountCatalogue.getClassification())
            .build();
}




   default AccountCatalogue toModel(AccountCatalogueEntity accountCatalogueEntity){
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

}
