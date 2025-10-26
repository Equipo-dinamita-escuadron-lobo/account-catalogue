package com.account_catalogue.paymentMethods.dataAccess.repository;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
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

}
