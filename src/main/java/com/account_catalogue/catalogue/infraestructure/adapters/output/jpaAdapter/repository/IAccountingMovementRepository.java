package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountingMovementEntity;

public interface IAccountingMovementRepository extends JpaRepository<AccountingMovementEntity, Long> {
    // NUEVO: Buscar movimientos por ID de cuenta contable
    List<AccountingMovementEntity> findByAccount(Long accountId);

    // NUEVO: Buscar movimientos por ID de tercero
    List<AccountingMovementEntity> findByThirdPartyId(Long thirdPartyId);
}
