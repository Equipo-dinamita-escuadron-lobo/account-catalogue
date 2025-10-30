package com.account_catalogue.accounting.domain.models;

import java.time.LocalDate;

import com.account_catalogue.accounting.domain.enums.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReplica {
    private Long id;
    private String factCode; 
    private Long totalValue;
    private Long totalPay;
    private Long thirdId;
    private Long pendingValue;
    private Long accountingAccount;
    private LocalDate expirationDate;
    private String entId;
    private InvoiceStatus status;
    private boolean active;


    /**
     * Lógica de negocio para castigar la cartera.
     */
    public void writeOff() {
        if (this.status == InvoiceStatus.PAID) 
            throw new IllegalStateException("Cannot write off a fully paid invoice. FactCode: " + this.factCode);

        if (this.status == InvoiceStatus.WRITTEN_OFF) 
            return;
        
        this.status = InvoiceStatus.WRITTEN_OFF;
        this.pendingValue = 0L;
    }
}
