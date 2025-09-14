package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptEntity;

@Repository
public interface IReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    Optional<ReceiptEntity> findByOriginalReceiptId(Long originalReceiptId);
}
