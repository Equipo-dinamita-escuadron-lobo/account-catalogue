package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountingMovementEntity;

public interface IAccountingMovementRepository extends JpaRepository<AccountingMovementEntity, Long> {
    
}
