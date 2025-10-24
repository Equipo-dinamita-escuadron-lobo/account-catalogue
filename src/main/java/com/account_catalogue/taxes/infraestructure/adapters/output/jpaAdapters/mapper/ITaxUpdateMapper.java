package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper;


import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITaxUpdateMapper {
    /**
     * Mapea un objeto Tax a un objeto TaxEntity.
     *
     * @param tax el objeto Tax a mapear
     * @return el objeto TaxEntity mapeado, o null si el objeto Tax es null
     */
    TaxEntity toEntity(Tax tax);

    /**
     * Mapea un objeto TaxEntity a un objeto Tax.
     *
     * @param taxEntity el TaxEntity a mapear
     * @return el modelo de dominio Tax mapeado, o null si el TaxEntity es null
     */
    Tax toModel(TaxEntity taxEntity);
}
