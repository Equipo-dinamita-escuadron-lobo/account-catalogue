package com.account_catalogue.taxes.application.output;

import java.util.List;
import org.springframework.data.domain.Page;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxSearchOutputPort {

    Tax getTax(String code, String idEnterprise);
    List<Tax> getActiveTaxes(String idEnterprise);
    Page<Tax> getTaxesPaginated(String idEnterprise, int page, int size, String sortField, String sortOrder);
    Page<Tax> getTaxesByDescriptionPaginated(String idEnterprise, String search, int page, int size, String sortField, String sortOrder);
    long countTaxesByEnterprise(String idEnterprise);
    long countTaxesByEnterpriseAndDescription(String idEnterprise, String search);
    Tax getTaxById(Long id);
    Tax getTaxByIdAndEnterprise(Long id, String idEnterprise);

}
