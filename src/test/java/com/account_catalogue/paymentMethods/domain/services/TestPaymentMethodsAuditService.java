package com.account_catalogue.paymentMethods.domain.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.commons.audit.annotation.Auditable;
import com.account_catalogue.commons.audit.annotation.OperationType;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;

@Service
public class TestPaymentMethodsAuditService {

    @Auditable(operationType = OperationType.CREATE, affectedTable = "PAYMENT_METHOD", moduleName = "PAYMENT_METHODS")
    public PaymentMethod create() {

        AccountCatalogue account = new AccountCatalogue();
        account.setId(100L);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setIdEnterprise("ENT-1");
        paymentMethod.setName("CASH");
        paymentMethod.setAccountingAccountEntity(account);
        paymentMethod.setAccountingAccount("1105");
        paymentMethod.setStatus(true);

        return paymentMethod;
    }

    @Auditable(operationType = OperationType.UPDATE, affectedTable = "PAYMENT_METHOD", moduleName = "PAYMENT_METHODS")
    public PaymentMethod update(PaymentMethodUpdateReq req) {

        AccountCatalogue account = new AccountCatalogue();
        account.setId(100L);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(req.getId());
        paymentMethod.setIdEnterprise(req.getIdEnterprise());
        paymentMethod.setName(req.getName());
        paymentMethod.setAccountingAccountEntity(account);
        paymentMethod.setAccountingAccount("1105");
        paymentMethod.setStatus(true);

        return paymentMethod;
    }

    @Auditable(operationType = OperationType.INACTIVATE, affectedTable = "PAYMENT_METHOD", moduleName = "PAYMENT_METHODS", idArgIndex = 0, enterpriseIdArgIndex = 1)
    public PaymentMethod changeState(Long id,
            String enterpriseId,
            Boolean newState) {

        AccountCatalogue account = new AccountCatalogue();
        account.setId(100L);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(id);
        paymentMethod.setIdEnterprise(enterpriseId);
        paymentMethod.setName("TRANSFER");
        paymentMethod.setAccountingAccountEntity(account);
        paymentMethod.setAccountingAccount("1105");
        paymentMethod.setStatus(newState);

        return paymentMethod;
    }

    @Auditable(operationType = OperationType.DELETE, affectedTable = "PAYMENT_METHOD", moduleName = "PAYMENT_METHODS", idArgIndex = 0, enterpriseIdArgIndex = 1)
    public PaymentMethod delete(Long id,
            String enterpriseId) {

        AccountCatalogue account = new AccountCatalogue();
        account.setId(100L);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(id);
        paymentMethod.setIdEnterprise(enterpriseId);
        paymentMethod.setName("TRANSFER");
        paymentMethod.setAccountingAccountEntity(account);
        paymentMethod.setAccountingAccount("1105");
        paymentMethod.setStatus(false);

        return paymentMethod;
    }
}
