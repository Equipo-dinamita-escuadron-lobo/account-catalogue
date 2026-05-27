package com.account_catalogue.copy.application.output;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;

public interface IBankAccountTargetRepositoryPort {
    BankAccountEntity guardar(BankAccountEntity entity);
}
