package com.account_catalogue.paymentMethods.presentation.DTO.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodRes {
    private Long id;
    private String name;
    private String accountingAccount; // Código completo (código - descripción)
    private Long accountingAccountId; // Solo el ID de la cuenta contable
    private Boolean status;
    private String idEnterprise;
}
