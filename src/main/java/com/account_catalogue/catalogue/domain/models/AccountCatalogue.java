package com.account_catalogue.catalogue.domain.models;

import java.util.List;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountCatalogue {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private NatureEnum nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;
    private AccountCatalogue  parent;
    private List<AccountCatalogue> children;
    private List<TaxEntity> depositAccounts;
    private List<TaxEntity>  refundAccounts;
    private Boolean crossing;
    private Boolean costCenter;
    private Boolean status;
}
