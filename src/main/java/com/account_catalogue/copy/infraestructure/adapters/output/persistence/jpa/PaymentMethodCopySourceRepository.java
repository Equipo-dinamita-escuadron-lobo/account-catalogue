package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentMethodCopySourceRepository extends JpaRepository<PaymentMethodEntity, Long> {

    @Query("SELECT pm FROM PaymentMethodEntity pm LEFT JOIN FETCH pm.accountingAccount WHERE pm.idEnterprise = :entOrigen")
    List<PaymentMethodEntity> findByEntOrigen(@Param("entOrigen") String entOrigen);
}
