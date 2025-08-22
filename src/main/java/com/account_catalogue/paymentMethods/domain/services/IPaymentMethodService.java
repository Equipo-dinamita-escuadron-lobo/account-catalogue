package com.account_catalogue.paymentMethods.domain.services;

import org.springframework.data.domain.Page;

import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodCreateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;

public interface IPaymentMethodService {

    PaymentMethod create(PaymentMethodCreateReq request);

    PaymentMethod update(PaymentMethodUpdateReq request);

    PaymentMethod findById(Long id, String idEnterprise);

    Page<PaymentMethod> findAllByEnterprise(String idEnterprise, int page, int size);

    Page<PaymentMethod> findAllByEnterpriseAndStatus(String idEnterprise, Boolean status, int page, int size);

    PaymentMethod changeState(Long id, String idEnterprise, Boolean newState);
}
