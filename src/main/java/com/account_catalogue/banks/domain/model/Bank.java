package com.account_catalogue.banks.domain.model;

import com.account_catalogue.banks.domain.enums.Currency;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bank {
    private Long id;
    private Integer codigo;
    private String nombre;
    private Currency moneda;
    @Builder.Default
    private Boolean status = true;
    @Builder.Default
    private Boolean isDeleted = false;
    private String idEnterprise;
}
