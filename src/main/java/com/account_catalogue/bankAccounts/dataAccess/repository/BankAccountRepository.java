package com.account_catalogue.bankAccounts.dataAccess.repository;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
    
    boolean existsByAccountNumberAndIdEnterpriseAndIsDeletedFalse(Long accountNumber, String idEnterprise);
    
    boolean existsByAccountNumberAndIdEnterpriseAndIdNotAndIsDeletedFalse(Long accountNumber, String idEnterprise, Long id);
    
    Optional<BankAccountEntity> findByIdAndIdEnterpriseAndIsDeletedFalse(Long id, String idEnterprise);
    
    Page<BankAccountEntity> findAllByIdEnterpriseAndIsDeletedFalse(String idEnterprise, Pageable pageable);
    
    Page<BankAccountEntity> findAllByIdEnterpriseAndStatusAndIsDeletedFalse(String idEnterprise, Boolean status, Pageable pageable);
    
    Page<BankAccountEntity> findAllByIdEnterpriseAndBankIdAndIsDeletedFalse(String idEnterprise, Long bankId, Pageable pageable);
    
    Page<BankAccountEntity> findAllByIdEnterpriseAndAccountTypeAndIsDeletedFalse(String idEnterprise, String accountType, Pageable pageable);
}
