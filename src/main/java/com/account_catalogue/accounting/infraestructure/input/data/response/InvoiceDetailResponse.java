package com.account_catalogue.accounting.infraestructure.input.data.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceDetailResponse {
    private Long id;
    private String factCode;
    private Long clientId;
    private LocalDate creationDate;
    private LocalDate expirationDate;
    private Long totalValue;
    private Long totalPay;
    private Long pendingValue;
    private String status;
    private Integer daysInArrears;
}
