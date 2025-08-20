package com.account_catalogue.taxes.application.output;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxCreateOutputPort {
    Tax createTax(TaxDTO tax);
}
