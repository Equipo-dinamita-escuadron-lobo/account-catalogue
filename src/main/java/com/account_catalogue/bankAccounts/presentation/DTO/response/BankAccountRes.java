package com.account_catalogue.bankAccounts.presentation.DTO.response;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.banks.presentation.DTO.response.BankRes;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountRes {
    private Long id;
    private Long accountNumber;
    private BankRes bank;
    private AccountType accountType;
    private String cuentaContable;
    private Boolean status;
    private String idEnterprise;
}
