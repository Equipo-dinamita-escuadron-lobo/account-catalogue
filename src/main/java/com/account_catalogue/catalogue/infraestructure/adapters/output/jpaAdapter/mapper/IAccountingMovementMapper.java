package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.account_catalogue.catalogue.domain.models.AccountingMovement;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountingMovementEntity;

@Mapper(componentModel = "spring")
public interface IAccountingMovementMapper {
    // Al convertir de Dominio a Entidad, ignoramos la referencia al padre 'accountingEntry'.
    @Mapping(target = "accountingEntry", ignore = true)
    AccountingMovementEntity toEntity(AccountingMovement domain);

    AccountingMovement toDomain(AccountingMovementEntity entity);
}
