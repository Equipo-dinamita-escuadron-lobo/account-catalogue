package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.banks.dataAccess.repository.BankRepository;
import com.account_catalogue.copy.application.output.IBankTargetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BankTargetRepositoryAdapter implements IBankTargetRepositoryPort {

    private final BankRepository jpaRepository;

    @Override
    @Transactional
    public BankEntity guardar(BankEntity entity) {
        return jpaRepository.save(entity);
    }
}
