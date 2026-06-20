package com.account_catalogue.copy.application.output;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;

public interface IPaymentMethodTargetRepositoryPort {
    PaymentMethodEntity guardar(PaymentMethodEntity entity);
}
