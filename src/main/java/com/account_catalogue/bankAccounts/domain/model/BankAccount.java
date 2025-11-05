package com.account_catalogue.bankAccounts.domain.model;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.banks.domain.model.Bank;
import lombok.*;

/**
 * @brief Modelo de dominio que representa una cuenta bancaria
 *
 * Contiene la información esencial de una cuenta bancaria incluyendo
 * número de cuenta, banco asociado, tipo y cuenta contable relacionada.
 */
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
    private Long accountingAccountId;
    @Builder.Default
    private Boolean status = true;
    private String idEnterprise;
}
