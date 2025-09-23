package com.account_catalogue.catalogue.domain.models;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptDetail {
    private Long id;
    private Long originalInvoiceId;
    private BigDecimal amountPaid;
    private String invoiceCode;
    private Long accountingAccount;
}
