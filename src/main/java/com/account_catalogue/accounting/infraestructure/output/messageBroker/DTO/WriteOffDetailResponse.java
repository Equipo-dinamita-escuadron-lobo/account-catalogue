package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WriteOffDetailResponse {
    private Long amountWrittenOff; // El monto que fue castigado
    private InvoiceSummaryResponse invoice; // El resumen de la factura
}
