package com.account_catalogue.accounting.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.account_catalogue.accounting.domain.enums.ProcessingStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Receipt {
    private Long id; //El ID único de este registro de recibo en la base de datos de contabilidad.
    private Long originalReceiptId; //El ID del recibo en el microservicio de origen (debt_payments). Esencial para trazabilidad
    private String receiptCode; 
    private String enterpriseId;  
    private Long receiptTypeId;  
    private Long thirdPartyId; 
    private Long paymentMethodId; 
    private Long paymentMethodAccount; // Añadido para la cuenta del método de pago
    private String status; 
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private String observations;
    private Long ledgerAccountId;
    private List<ReceiptDetail> details;
    private ProcessingStatus processingStatus;
}
