package com.chartaccounts.infraestructure.adapters.output.jpaAdapter.mapper;

import com.chartaccounts.domain.models.AccountCatalogue;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IAccountCatalogueCreateMapper {
    AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue);
    AccountCatalogue toModel(AccountCatalogueEntity accountCatalogueEntity);

}
