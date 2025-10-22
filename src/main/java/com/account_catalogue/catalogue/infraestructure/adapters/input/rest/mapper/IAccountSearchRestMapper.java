package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IAccountSearchRestMapper {

    default AccountCatalogueListRes toAccountCatalogueListRes(AccountCatalogue accountCatalogue) {

        if (accountCatalogue == null) {
            return null;
        }
        List<AccountCatalogueListRes> children = accountCatalogue.getChildren().stream()
                .map(this::toAccountCatalogueListRes)
                .collect(Collectors.toList());
        return AccountCatalogueListRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature() != null ? accountCatalogue.getNature().getState() : null)
                .financialStatus(accountCatalogue.getFinancialStatus() != null ? accountCatalogue.getFinancialStatus().getState() : null)
                .classification(accountCatalogue.getClassification() != null ? accountCatalogue.getClassification().getState() : null)
                .crossing(accountCatalogue.getCrossing())
                .costCenter(accountCatalogue.getCostCenter())
                .status(accountCatalogue.getStatus())
                .children(children)
                .parent(accountCatalogue.getParent() != null ? accountCatalogue.getParent().getCode() : null)
                .build();
    }
}
