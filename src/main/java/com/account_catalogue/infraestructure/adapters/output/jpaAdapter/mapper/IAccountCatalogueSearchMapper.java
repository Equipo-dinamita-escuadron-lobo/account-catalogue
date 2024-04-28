package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection.IAccountCatalogueInfoProjection;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface IAccountCatalogueSearchMapper
{
     default List<AccountCatalogueInfoDTO> toModelListAccountCatalogue(List<IAccountCatalogueInfoProjection> listAccountCatalogueInfo){
     if(listAccountCatalogueInfo==null){
           return null;
       }
       return listAccountCatalogueInfo.stream()
               .map(accountCatalogue -> {
                   AccountCatalogueInfoDTO accountInfoDto=AccountCatalogueInfoDTO.builder()
                           .id(accountCatalogue.getId())
                           .code(accountCatalogue.getCode())
                           .description(accountCatalogue.getDescription())
                           .build();

                   return accountInfoDto;

               } )
               .toList();
   }
}
