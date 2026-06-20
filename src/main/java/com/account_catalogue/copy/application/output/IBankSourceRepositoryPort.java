package com.account_catalogue.copy.application.output;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;

import java.util.List;

public interface IBankSourceRepositoryPort {
    List<BankEntity> findByEntOrigen(String entOrigen);
}
