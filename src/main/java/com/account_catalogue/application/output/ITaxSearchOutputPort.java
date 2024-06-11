package com.account_catalogue.application.output;

import com.account_catalogue.domain.models.Tax;

import java.util.List;

public interface ITaxSearchOutputPort {

    Tax getTax(String code);
    List<Tax> getTaxes();

}
