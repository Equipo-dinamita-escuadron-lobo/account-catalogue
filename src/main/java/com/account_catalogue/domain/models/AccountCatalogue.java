package com.account_catalogue.domain.models;

import java.util.List;
import java.util.Set;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;

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
    private List<Tax> taxes;
}
