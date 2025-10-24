package com.account_catalogue.bankAccounts.dataAccess.mapper;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountDataMapper {

    @Mapping(target = "accountingAccountId", source = "accountingAccount.id")
    BankAccount toDomain(BankAccountEntity entity);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "bank.tenantId", ignore = true)
    @Mapping(target = "accountingAccount", ignore = true)
    BankAccountEntity toEntity(BankAccount domain);
}
