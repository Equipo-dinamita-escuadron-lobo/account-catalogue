package com.account_catalogue.bankAccounts.domain.model;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.banks.domain.model.Bank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {
    private Long id;
    private Long accountNumber;
    private Bank bank;
    private AccountType accountType;
    private String cuentaContable;
    @Builder.Default
    private Boolean status = true;
    private String idEnterprise;
}
