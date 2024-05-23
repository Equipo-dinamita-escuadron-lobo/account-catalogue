package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IAccountSearchRestMapper {

    default  AccountCatalogueListRes toAccountCatalogueListRes(AccountCatalogue accountCatalogue){

        if(accountCatalogue==null){
            return null;
        }
        List<AccountCatalogueListRes> children = accountCatalogue.getChildren().stream()
                .map(this::toAccountCatalogueListRes)
                .collect(Collectors.toList());
        return AccountCatalogueListRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature().getState())
                .financialStatus(accountCatalogue.getFinancialStatus().getState())
                .classification(accountCatalogue.getClassification().getState())
                .children(children)
                .parent(accountCatalogue.getParent().getCode() == null ? null : accountCatalogue.getParent().getCode())
                .build();
    }
}
