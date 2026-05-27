package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankAccountCopySourceRepository extends JpaRepository<BankAccountEntity, Long> {

    @Query("SELECT ba FROM BankAccountEntity ba JOIN FETCH ba.bank JOIN FETCH ba.accountingAccount WHERE ba.idEnterprise = :entOrigen")
    List<BankAccountEntity> findByEntOrigen(@Param("entOrigen") String entOrigen);
}
