package com.account_catalogue.domain.models;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountTax {
    private Long id;
    private AccountCatalogue accountCatalogue;
    private Tax tax;
}
