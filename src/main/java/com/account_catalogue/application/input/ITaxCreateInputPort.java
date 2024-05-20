package com.account_catalogue.application.input;

import com.account_catalogue.domain.models.Tax;

public interface ITaxCreateInputPort {
    Tax createTax(Tax tax);
}
