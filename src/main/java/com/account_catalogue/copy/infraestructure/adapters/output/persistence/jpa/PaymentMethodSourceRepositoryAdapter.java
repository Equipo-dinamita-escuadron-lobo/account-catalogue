package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.copy.application.output.IPaymentMethodSourceRepositoryPort;
import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMethodSourceRepositoryAdapter implements IPaymentMethodSourceRepositoryPort {

    private final PaymentMethodCopySourceRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodEntity> findByEntOrigen(String entOrigen) {
        return jpaRepository.findByEntOrigen(entOrigen);
    }
}
