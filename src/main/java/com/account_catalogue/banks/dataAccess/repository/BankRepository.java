package com.account_catalogue.banks.dataAccess.repository;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    
    boolean existsByCodigoAndIdEnterprise(String codigo, String idEnterprise);
    
    boolean existsByNombreAndIdEnterprise(String nombre, String idEnterprise);
    
    boolean existsByCodigoAndIdEnterpriseAndIdNot(String codigo, String idEnterprise, Long id);
    
    boolean existsByNombreAndIdEnterpriseAndIdNot(String nombre, String idEnterprise, Long id);
    
    Optional<BankEntity> findByIdAndIdEnterprise(Long id, String idEnterprise);
    
    Page<BankEntity> findAllByIdEnterprise(String idEnterprise, Pageable pageable);
    
    Page<BankEntity> findAllByIdEnterpriseAndStatus(String idEnterprise, Boolean status, Pageable pageable);
}
