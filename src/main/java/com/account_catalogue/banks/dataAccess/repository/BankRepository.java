package com.account_catalogue.banks.dataAccess.repository;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    
    boolean existsByCodigoAndIdEnterprise(String codigo, String idEnterprise);
    
    boolean existsByNombreAndIdEnterprise(String nombre, String idEnterprise);
    
    boolean existsByCodigoAndIdEnterpriseAndIdNot(String codigo, String idEnterprise, Long id);
    
    boolean existsByNombreAndIdEnterpriseAndIdNot(String nombre, String idEnterprise, Long id);
    
    Optional<BankEntity> findByIdAndIdEnterprise(Long id, String idEnterprise);
    
    Page<BankEntity> findAllByIdEnterprise(String idEnterprise, Pageable pageable);
    
    Page<BankEntity> findAllByIdEnterpriseAndStatus(String idEnterprise, Boolean status, Pageable pageable);

    long countByIdEnterpriseAndStatus(String idEnterprise, Boolean status);

    long countByIdEnterprise(String idEnterprise);

    @Query("SELECT b FROM BankEntity b WHERE b.idEnterprise = ?1 AND (LOWER(b.codigo) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(b.nombre) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<BankEntity> findByIdEnterpriseAndSearch(String idEnterprise, String search, Pageable pageable);

    @Query("SELECT COUNT(b) FROM BankEntity b WHERE b.idEnterprise = ?1 AND (LOWER(b.codigo) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(b.nombre) LIKE LOWER(CONCAT('%', ?2, '%')))")
    long countByIdEnterpriseAndSearch(String idEnterprise, String search);
}
