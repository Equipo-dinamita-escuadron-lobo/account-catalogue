package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PaymentVoucherEventDto(Long id, String voucherNumber, String enterpriseId, LocalDate issueDate,
        String status, Long paymentMethodId, Long bankAccountId, BigDecimal total, String observations,
        String tenantId, List<Detail> details) {
    public record Detail(Long supplierId, Long invoiceId, String invoiceReference, Long payableAccountId,
                         String payableAccountCode, BigDecimal amountPaid) {}
}
