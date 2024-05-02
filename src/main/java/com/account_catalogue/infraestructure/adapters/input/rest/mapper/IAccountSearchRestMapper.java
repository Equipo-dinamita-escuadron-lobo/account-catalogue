package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueSearchRes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface IAccountSearchRestMapper {
    List<AccountCatalogueSearchRes> toListAccountResponse(List<AccountCatalogueInfoDTO> accountCatalogue);

    
    AccountCatalogueListRes toAccountCatalogueListRes(AccountCatalogue accountCatalogue);
}
