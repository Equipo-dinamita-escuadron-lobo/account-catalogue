package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueSearchRes;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IAccountSearchRestMapper {
    List<AccountCatalogueSearchRes> toListAccountResponse(List<AccountCatalogueInfoDTO> accountCatalogue);

    
    default  AccountCatalogueListRes toAccountCatalogueListRes(AccountCatalogue accountCatalogue){

        if(accountCatalogue==null){
            return null;
        }
        List<AccountCatalogueListRes> children = accountCatalogue.getChildren().stream()
                .map(this::toAccountCatalogueListRes)
                .collect(Collectors.toList());
        return AccountCatalogueListRes.builder()
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature().getState())
                .financialStatus(accountCatalogue.getFinancialStatus().getState())
                .classification(accountCatalogue.getClassification().getState())
                .children(children)
                .build();
    }
}
