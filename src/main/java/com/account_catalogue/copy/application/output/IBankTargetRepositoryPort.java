package com.account_catalogue.copy.application.output;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;

public interface IBankTargetRepositoryPort {
    BankEntity guardar(BankEntity entity);
}
