package com.account_catalogue.catalogue.domain.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptDetail {
    private Long id;
    private Long originalInvoiceId;
    private Long amountPaid;
    private String invoiceCode;
    private Long accountingAccount;
    
}
