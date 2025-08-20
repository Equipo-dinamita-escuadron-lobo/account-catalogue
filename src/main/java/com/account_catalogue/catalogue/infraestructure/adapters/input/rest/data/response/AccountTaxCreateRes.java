package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AccountTaxCreateRes {
    private Long id;
    private AccountCatalogue accountCatalogue;
    private Tax tax;
}
