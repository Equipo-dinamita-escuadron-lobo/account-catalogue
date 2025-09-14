package com.account_catalogue.catalogue.domain.models;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Receipt {
    private Long id;
    private Long originalReceiptId;
    private String receiptCode;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private String status; 
    private LocalDate issueDate;
    private Long totalAmount;
    private String observations;
    private List<ReceiptDetail> details;
    private String processingStatus; 

}
