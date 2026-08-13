package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

import java.math.BigDecimal;
import java.util.List;

public record PayableWriteOffEventDto(Long id, String enterpriseId, String reason, Long counterpartAccountId,
        String counterpartAccountCode, BigDecimal total, String tenantId, List<Detail> details) {
    public record Detail(Long supplierId, Long invoiceId, Long payableAccountId, String payableAccountCode, BigDecimal amount) {}
}
