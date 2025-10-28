package com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.entity.ReceiptDetailEntity;

@Repository
public interface  IReceiptDetailRepository extends JpaRepository<ReceiptDetailEntity, Long> {
    
}
