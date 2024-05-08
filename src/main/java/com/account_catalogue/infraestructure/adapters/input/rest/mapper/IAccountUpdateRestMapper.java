package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface  IAccountUpdateRestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    AccountCatalogue toDomain(AccountCatalogueUpdateReq accountCatalogueUpdateReq);

    AccountCatalogueUpdateRes toUpdateResponse(AccountCatalogue accountCatalogue);
}
