package com.account_catalogue.copy.application.output;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;

import java.util.List;

public interface IPaymentMethodSourceRepositoryPort {
    List<PaymentMethodEntity> findByEntOrigen(String entOrigen);
}
