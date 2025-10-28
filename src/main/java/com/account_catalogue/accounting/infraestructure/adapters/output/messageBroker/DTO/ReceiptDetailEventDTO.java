package com.account_catalogue.accounting.infraestructure.adapters.output.messageBroker.DTO;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailEventDTO {
    private Long invoiceId;
    private BigDecimal amountPaid;
    private String invoiceCode;
    private Long accountingAccount;
}
