package com.account_catalogue.bankAccounts.domain.mapper;

import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountCreateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountUpdateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.response.BankAccountRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountDomainMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bank", ignore = true)
    BankAccount toDomain(BankAccountCreateReq request);
    
    @Mapping(target = "bank", ignore = true)
    BankAccount toDomain(BankAccountUpdateReq request);
    
    BankAccountRes toRes(BankAccount domain);
}
