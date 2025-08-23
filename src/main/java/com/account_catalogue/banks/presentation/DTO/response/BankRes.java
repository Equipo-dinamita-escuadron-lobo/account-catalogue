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
    private Integer codigo;
    private String nombre;
    private Currency moneda;
    private Boolean status;
    @Builder.Default
    private Boolean isDeleted = false;
    private String idEnterprise;
}
