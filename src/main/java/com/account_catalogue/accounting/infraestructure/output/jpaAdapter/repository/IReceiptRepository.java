package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.ReceiptEntity;

@Repository
public interface IReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    Optional<ReceiptEntity> findByOriginalReceiptId(Long originalReceiptId);
    Optional<ReceiptEntity> findByReceiptCode(String receiptCode);
    boolean existsByReceiptCode(String receiptCode);
}
