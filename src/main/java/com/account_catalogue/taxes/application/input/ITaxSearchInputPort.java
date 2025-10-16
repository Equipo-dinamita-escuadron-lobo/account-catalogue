package com.account_catalogue.taxes.application.input;

import java.util.List;
import org.springframework.data.domain.Page;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxSearchInputPort {
    Tax getTax(String code, String idEnterprise);
    List<Tax> getActiveTaxes(String idEnterprise);
    Page<Tax> getTaxesPaginated(String idEnterprise, int page, int size, String sortField, String sortOrder);
    Page<Tax> getTaxesByCodeOrDescriptionPaginated(String idEnterprise, String search, int page, int size, String sortField, String sortOrder);
    long countTaxesByEnterprise(String idEnterprise);
    long countTaxesByEnterpriseAndCodeOrDescription(String idEnterprise, String search);
}
