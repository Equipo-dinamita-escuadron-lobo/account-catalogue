package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.copy.application.output.IAccountTargetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de escritura de cuentas destino — implementa IAccountTargetRepositoryPort.
 * El caller debe haber seteado TenantContext.setTenantId(entDestino) antes de invocar.
 */
@Component
@RequiredArgsConstructor
public class AccountTargetRepositoryAdapter implements IAccountTargetRepositoryPort {

    private final IAccountCatalogueRepository jpaRepository;

    @Override
    @Transactional
    public AccountCatalogueEntity guardar(AccountCatalogueEntity entity) {
        return jpaRepository.save(entity);
    }
}
