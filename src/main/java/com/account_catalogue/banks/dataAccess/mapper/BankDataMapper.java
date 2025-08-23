package com.account_catalogue.banks.dataAccess.mapper;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.banks.domain.model.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankDataMapper {
    
    Bank toDomain(BankEntity entity);
    
    @Mapping(target = "tenantId", ignore = true)
    BankEntity toEntity(Bank domain);
}
