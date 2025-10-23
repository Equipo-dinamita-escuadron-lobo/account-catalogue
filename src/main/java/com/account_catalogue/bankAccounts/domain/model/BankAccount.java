package com.account_catalogue.bankAccounts.domain.model;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
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
    private AccountCatalogue accountingAccount;
    @Builder.Default
    private Boolean status = true;
    private String idEnterprise;
}
