package com.account_catalogue.banks.presentation.DTO.response;

import com.account_catalogue.banks.domain.enums.Currency;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankRes {
    private Long id;
    private String code;
    private String name;
    private Currency currency;
    private Boolean status;
    private String idEnterprise;
}
