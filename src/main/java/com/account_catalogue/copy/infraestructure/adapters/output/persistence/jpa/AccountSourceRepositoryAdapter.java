package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.copy.application.output.IAccountSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador de lectura de cuentas origen — implementa IAccountSourceRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class AccountSourceRepositoryAdapter implements IAccountSourceRepositoryPort {

    private final AccountCopySourceRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AccountCatalogueEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte) {
        return jpaRepository.findByEntOrigenBeforeSnapshot(entOrigen, snapshotCorte);
    }
}
