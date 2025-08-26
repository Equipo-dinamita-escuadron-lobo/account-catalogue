package com.account_catalogue.taxes.application.output;

import java.util.List;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxSearchOutputPort {

    Tax getTax(String code, String idEnterprise);
    List<Tax> getTaxes(String idEnterprise);
    Tax getTaxById(Long id);
    Tax getTaxByIdAndEnterprise(Long id, String idEnterprise);

}
