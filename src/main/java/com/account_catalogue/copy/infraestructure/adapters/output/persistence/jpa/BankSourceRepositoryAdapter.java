package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.copy.application.output.IBankSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BankSourceRepositoryAdapter implements IBankSourceRepositoryPort {

    private final BankCopySourceRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BankEntity> findByEntOrigen(String entOrigen) {
        return jpaRepository.findByEntOrigen(entOrigen);
    }
}
