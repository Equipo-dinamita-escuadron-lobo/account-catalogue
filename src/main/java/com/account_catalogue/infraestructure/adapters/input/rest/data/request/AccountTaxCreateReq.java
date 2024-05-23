package com.account_catalogue.infraestructure.adapters.input.rest.data.request;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.domain.models.Tax;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AccountTaxCreateReq {
    private AccountCatalogue accountCatalogue;
    private Tax tax;

}
