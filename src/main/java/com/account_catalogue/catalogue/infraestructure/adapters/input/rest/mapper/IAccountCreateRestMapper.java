package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AccountCatalogueCreateRes;

public interface IAccountCreateRestMapper {
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);

    AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq, AccountCatalogue aux);

}
