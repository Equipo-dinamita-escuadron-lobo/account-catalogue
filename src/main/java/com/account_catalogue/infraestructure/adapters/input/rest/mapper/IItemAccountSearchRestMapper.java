package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import org.mapstruct.Mapper;


@Mapper
public interface IItemAccountSearchRestMapper {
    /**
     * Este metodo transforma un objeto AccountCatalogue en un objeto
     * ItemAccountCatalogueSearchRes que es el objeto que se utiliza para
     * devolver el detalle de la cuenta en la API REST.
     *
     * @param accountCatalogue el objeto AccountCatalogue a transformar.
     * @return el objeto ItemAccountCatalogueSearchRes que representa el objeto
     *         AccountCatalogue transformado.
     */
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
               .parent(accountCatalogue.getParent().getCode() == null ? null : accountCatalogue.getParent().getCode())
               .build();
   }
}
