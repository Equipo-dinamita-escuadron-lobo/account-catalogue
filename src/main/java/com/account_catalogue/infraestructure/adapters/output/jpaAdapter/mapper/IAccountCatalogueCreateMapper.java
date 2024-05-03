package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

public interface IAccountCatalogueCreateMapper {

   AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue, AccountCatalogueEntity parent);

   AccountCatalogue toModel(AccountCatalogueEntity accountCatalogueEntity);

}
