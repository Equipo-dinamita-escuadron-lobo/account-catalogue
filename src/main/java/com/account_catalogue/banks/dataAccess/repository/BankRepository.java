package com.account_catalogue.banks.dataAccess.repository;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    
    boolean existsByCodigoAndIdEnterpriseAndIsDeletedFalse(Integer codigo, String idEnterprise);
    
    boolean existsByNombreAndIdEnterpriseAndIsDeletedFalse(String nombre, String idEnterprise);
    
    boolean existsByCodigoAndIdEnterpriseAndIdNotAndIsDeletedFalse(Integer codigo, String idEnterprise, Long id);
    
    boolean existsByNombreAndIdEnterpriseAndIdNotAndIsDeletedFalse(String nombre, String idEnterprise, Long id);
    
    Optional<BankEntity> findByIdAndIdEnterpriseAndIsDeletedFalse(Long id, String idEnterprise);
    
    Page<BankEntity> findAllByIdEnterpriseAndIsDeletedFalse(String idEnterprise, Pageable pageable);
    
    Page<BankEntity> findAllByIdEnterpriseAndStatusAndIsDeletedFalse(String idEnterprise, Boolean status, Pageable pageable);
}
