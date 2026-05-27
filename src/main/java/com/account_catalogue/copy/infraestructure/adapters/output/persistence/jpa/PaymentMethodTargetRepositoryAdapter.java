package com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa;

import com.account_catalogue.copy.application.output.IPaymentMethodTargetRepositoryPort;
import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.dataAccess.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentMethodTargetRepositoryAdapter implements IPaymentMethodTargetRepositoryPort {

    private final PaymentMethodRepository jpaRepository;

    @Override
    @Transactional
    public PaymentMethodEntity guardar(PaymentMethodEntity entity) {
        return jpaRepository.save(entity);
    }
}
