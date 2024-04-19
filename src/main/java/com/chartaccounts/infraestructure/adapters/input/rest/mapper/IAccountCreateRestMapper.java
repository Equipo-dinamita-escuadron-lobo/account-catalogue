package com.chartaccounts.infraestructure.adapters.input.rest.mapper;

import com.chartaccounts.domain.models.AccountCatalogue;
import com.chartaccounts.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.chartaccounts.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import org.mapstruct.Mapper;



@Mapper
public interface IAccountCreateRestMapper {
    AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq);
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);
}
