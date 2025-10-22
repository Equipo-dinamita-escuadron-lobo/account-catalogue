package com.account_catalogue.banks.domain.mapper;

import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.banks.presentation.DTO.request.BankCreateReq;
import com.account_catalogue.banks.presentation.DTO.request.BankUpdateReq;
import com.account_catalogue.banks.presentation.DTO.response.BankRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankDomainMapper {
    
    @Mapping(target = "id", ignore = true)
    Bank toDomain(BankCreateReq request);
    
    Bank toDomain(BankUpdateReq request);
    
    BankRes toRes(Bank domain);
}
