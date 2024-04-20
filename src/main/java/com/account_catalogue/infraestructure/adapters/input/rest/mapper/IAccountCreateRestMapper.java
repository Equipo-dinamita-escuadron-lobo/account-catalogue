package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import org.mapstruct.Mapper;



@Mapper
public interface IAccountCreateRestMapper {
    AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq);
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);
}
