package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountingEntryEntity;

public interface IAccountingEntryRepository extends JpaRepository<AccountingEntryEntity, Long>{
    
    Optional<AccountingEntryEntity> findBySourceDocumentId(Long sourceDocumentId);

}
