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
    private String accountingAccount;
    private Boolean status;
    private String idEnterprise;
}
