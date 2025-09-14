package com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailEventDTO {
    private Long invoiceId; // Corresponde a originalInvoiceId en el dominio
    private Long amountPaid;
    private String invoiceCode;
    private Long accountingAccount;
}
