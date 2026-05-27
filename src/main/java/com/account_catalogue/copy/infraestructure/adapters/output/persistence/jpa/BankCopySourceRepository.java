package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankCopySourceRepository extends JpaRepository<BankEntity, Long> {

    @Query("SELECT b FROM BankEntity b WHERE b.idEnterprise = :entOrigen")
    List<BankEntity> findByEntOrigen(@Param("entOrigen") String entOrigen);
}
