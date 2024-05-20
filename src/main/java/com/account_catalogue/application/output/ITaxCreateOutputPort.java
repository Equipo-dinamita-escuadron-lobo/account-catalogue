package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.Tax;

public interface ITaxCreateOutputPort {
    Tax createTax(Tax tax);
}
