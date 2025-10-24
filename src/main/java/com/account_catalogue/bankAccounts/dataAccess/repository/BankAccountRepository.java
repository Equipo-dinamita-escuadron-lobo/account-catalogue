package com.account_catalogue.bankAccounts.dataAccess.repository;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
    
    boolean existsByAccountNumberAndIdEnterprise(Long accountNumber, String idEnterprise);
    
    boolean existsByAccountNumberAndIdEnterpriseAndIdNot(Long accountNumber, String idEnterprise, Long id);
    
    Optional<BankAccountEntity> findByIdAndIdEnterprise(Long id, String idEnterprise);
    
    Page<BankAccountEntity> findAllByIdEnterprise(String idEnterprise, Pageable pageable);

    Page<BankAccountEntity> findAllByIdEnterpriseAndStatus(String idEnterprise, Boolean status, Pageable pageable);

    long countByIdEnterprise(String idEnterprise);

    long countByIdEnterpriseAndStatus(String idEnterprise, Boolean status);

    Page<BankAccountEntity> findAllByIdEnterpriseAndBankId(String idEnterprise, Long bankId, Pageable pageable);

    Page<BankAccountEntity> findAllByIdEnterpriseAndAccountType(String idEnterprise, String accountType, Pageable pageable);

    @Query("SELECT ba FROM BankAccountEntity ba WHERE ba.idEnterprise = ?1 AND CAST(ba.accountNumber AS string) LIKE LOWER(CONCAT('%', ?2, '%'))")
    Page<BankAccountEntity> findByIdEnterpriseAndAccountNumberSearch(String idEnterprise, String search, Pageable pageable);

    @Query("SELECT COUNT(ba) FROM BankAccountEntity ba WHERE ba.idEnterprise = ?1 AND CAST(ba.accountNumber AS string) LIKE LOWER(CONCAT('%', ?2, '%'))")
    long countByIdEnterpriseAndAccountNumberSearch(String idEnterprise, String search);
}
