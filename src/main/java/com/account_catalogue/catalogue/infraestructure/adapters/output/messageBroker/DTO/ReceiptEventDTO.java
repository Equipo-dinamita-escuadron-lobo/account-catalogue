package com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO;


import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptEventDTO {
    private Long id;
    private String receiptCode;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private String status;
    private LocalDate issueDate;
    private Long totalAmount;
    private String observations;
    private List<ReceiptDetailEventDTO> details;
}