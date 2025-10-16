package com.account_catalogue.paymentMethods.domain.model;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
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
    private AccountCatalogue accountingAccountEntity;
    private Boolean status;
    private String idEnterprise;
}
