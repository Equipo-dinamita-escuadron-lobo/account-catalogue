package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;

public interface  IAccountUpdateRestMapper {

    AccountCatalogue toDomain(AccountCatalogueUpdateReq accountCatalogueUpdateReq);

    AccountCatalogueUpdateRes toUpdateResponse(AccountCatalogue accountCatalogue);
}
