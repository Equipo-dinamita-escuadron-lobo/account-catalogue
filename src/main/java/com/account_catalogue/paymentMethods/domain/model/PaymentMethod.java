package com.account_catalogue.paymentMethods.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {
    private Long id;
    private String name;
    private String accountingAccount;
    private Boolean status;
    @Builder.Default
    private Boolean isDeleted = false;
    private String idEnterprise;
}
