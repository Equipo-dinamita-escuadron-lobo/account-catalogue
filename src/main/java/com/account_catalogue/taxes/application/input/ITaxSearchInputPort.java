package com.account_catalogue.taxes.application.input;

import java.util.List;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxSearchInputPort {
    Tax getTax(String code, String idEnterprise);
    List<Tax> getTaxes(String idEnterprise);
}
