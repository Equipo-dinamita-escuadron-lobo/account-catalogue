package com.account_catalogue.paymentMethods.dataAccess.repository;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {    
   
    boolean existsByNameAndIdEnterprise(String name, String idEnterprise);
    
    boolean existsByNameAndIdEnterpriseAndIdNot(String name, String idEnterprise, Long id);
    
    @EntityGraph(attributePaths = "accountingAccount")
    Optional<PaymentMethodEntity> findByIdAndIdEnterprise(Long id, String idEnterprise);

    @EntityGraph(attributePaths = "accountingAccount")
    Page<PaymentMethodEntity> findAllByIdEnterprise(String idEnterprise, Pageable pageable);

    @EntityGraph(attributePaths = "accountingAccount")
    Page<PaymentMethodEntity> findAllByIdEnterpriseAndStatus(String idEnterprise, Boolean status, Pageable pageable);

    long countByIdEnterprise(String idEnterprise);

    long countByIdEnterpriseAndStatus(String idEnterprise, Boolean status);

    @Query("SELECT pm FROM PaymentMethodEntity pm LEFT JOIN pm.accountingAccount ac WHERE pm.idEnterprise = ?1 AND (LOWER(pm.name) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(ac.code) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(ac.description) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<PaymentMethodEntity> findByIdEnterpriseAndSearch(String idEnterprise, String search, Pageable pageable);

    @Query("SELECT COUNT(pm) FROM PaymentMethodEntity pm LEFT JOIN pm.accountingAccount ac WHERE pm.idEnterprise = ?1 AND (LOWER(pm.name) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(ac.code) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(ac.description) LIKE LOWER(CONCAT('%', ?2, '%')))")
    long countByIdEnterpriseAndSearch(String idEnterprise, String search);

    boolean existsByAccountingAccountIdAndIdEnterprise(Long accountingAccountId, String idEnterprise);

}
