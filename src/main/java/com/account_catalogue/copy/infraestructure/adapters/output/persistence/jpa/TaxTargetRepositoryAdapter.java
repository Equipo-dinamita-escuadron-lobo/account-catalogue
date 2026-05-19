package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.copy.application.output.ITaxTargetRepositoryPort;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de escritura de impuestos destino — implementa ITaxTargetRepositoryPort.
 * El caller debe haber seteado TenantContext.setTenantId(entDestino) antes de invocar.
 */
@Component
@RequiredArgsConstructor
public class TaxTargetRepositoryAdapter implements ITaxTargetRepositoryPort {

    private final ITaxRepository jpaRepository;

    @Override
    @Transactional
    public TaxEntity guardar(TaxEntity entity) {
        return jpaRepository.save(entity);
    }
}
