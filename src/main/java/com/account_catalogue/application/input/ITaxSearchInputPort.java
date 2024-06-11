package com.account_catalogue.application.input;

import com.account_catalogue.domain.models.Tax;

import java.util.List;

public interface ITaxSearchInputPort {
    Tax getTax(String code);
    List<Tax> getTaxes();
}
