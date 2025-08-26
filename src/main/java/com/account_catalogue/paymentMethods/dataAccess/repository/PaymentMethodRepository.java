package com.account_catalogue.paymentMethods.dataAccess.repository;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {    
   
    boolean existsByNameAndIdEnterpriseAndIsDeletedFalse(String name, String idEnterprise);
    
    boolean existsByNameAndIdEnterpriseAndIdNotAndIsDeletedFalse(String name, String idEnterprise, Long id);
    
    Optional<PaymentMethodEntity> findByIdAndIdEnterpriseAndIsDeletedFalse(Long id, String idEnterprise);
    
    Page<PaymentMethodEntity> findAllByIdEnterpriseAndIsDeletedFalse(String idEnterprise, Pageable pageable);
    
    Page<PaymentMethodEntity> findAllByIdEnterpriseAndStatusAndIsDeletedFalse(String idEnterprise, Boolean status, Pageable pageable);

}
