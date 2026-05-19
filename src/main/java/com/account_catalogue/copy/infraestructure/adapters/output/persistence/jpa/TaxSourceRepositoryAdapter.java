package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.copy.application.output.ITaxSourceRepositoryPort;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador de lectura de impuestos origen — implementa ITaxSourceRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class TaxSourceRepositoryAdapter implements ITaxSourceRepositoryPort {

    private final TaxCopySourceRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaxEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte) {
        return jpaRepository.findByEntOrigenBeforeSnapshot(entOrigen, snapshotCorte);
    }
}
