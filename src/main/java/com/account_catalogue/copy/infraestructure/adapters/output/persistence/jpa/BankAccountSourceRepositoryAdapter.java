package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.copy.application.output.IBankAccountSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BankAccountSourceRepositoryAdapter implements IBankAccountSourceRepositoryPort {

    private final BankAccountCopySourceRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountEntity> findByEntOrigen(String entOrigen) {
        return jpaRepository.findByEntOrigen(entOrigen);
    }
}
