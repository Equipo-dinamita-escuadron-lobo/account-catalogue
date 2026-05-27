package com.account_catalogue.copy.application.output;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;

import java.util.List;

public interface IBankAccountSourceRepositoryPort {
    List<BankAccountEntity> findByEntOrigen(String entOrigen);
}
