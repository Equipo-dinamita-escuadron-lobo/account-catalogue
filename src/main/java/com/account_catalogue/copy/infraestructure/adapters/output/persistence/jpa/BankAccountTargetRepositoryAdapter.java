package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.bankAccounts.dataAccess.repository.BankAccountRepository;
import com.account_catalogue.copy.application.output.IBankAccountTargetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BankAccountTargetRepositoryAdapter implements IBankAccountTargetRepositoryPort {

    private final BankAccountRepository jpaRepository;

    @Override
    @Transactional
    public BankAccountEntity guardar(BankAccountEntity entity) {
        return jpaRepository.save(entity);
    }
}
