package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import org.mapstruct.Mapper;


@Mapper
public interface IItemAccountSearchRestMapper {
   default ItemAccountCatalogueSearchRes toItemAccountCatalogueSearch(AccountCatalogue accountCatalogue){
       if(accountCatalogue==null){
           return null;
       }
       return ItemAccountCatalogueSearchRes.builder()
                .id(accountCatalogue.getId())
               .code(accountCatalogue.getCode())
               .description(accountCatalogue.getDescription())
               .nature(accountCatalogue.getNature().getState())
               .financialStatus(accountCatalogue.getFinancialStatus().getState())
               .classification(accountCatalogue.getClassification().getState())
               .build();
   }
}
