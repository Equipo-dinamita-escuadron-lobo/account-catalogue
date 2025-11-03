package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvoiceSummaryResponse {
    private Long id;
    private String factCode;
    private Long totalValue;
    private Long pendingValue; // El saldo pendiente ANTES del castigo
    private LocalDate expirationDate;
    private Long accountingAccount;// Codigo de la uenta contable asociada a la factura
}
