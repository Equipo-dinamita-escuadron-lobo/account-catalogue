package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.impl;

import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.util.AdjustEnumAccount;

@Component
public class AccountUpdateRestMapper implements IAccountUpdateRestMapper {

    private AdjustEnumAccount adjustEnum = new AdjustEnumAccount();

    @Override
    public AccountCatalogue toDomain(AccountCatalogueUpdateReq accountCatalogueUpdateReq) {
        if(accountCatalogueUpdateReq == null) {
            return null;
        }

        return AccountCatalogue.builder()
                .code(accountCatalogueUpdateReq.getCode())
                .description(accountCatalogueUpdateReq.getDescription())
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueUpdateReq.getFinancialStatus()))
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueUpdateReq.getNature()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueUpdateReq.getClassification()))
                .build();
    }

    @Override
    public AccountCatalogueUpdateRes toUpdateResponse(AccountCatalogue accountCatalogue) {
        if(accountCatalogue == null) {
            return null;
        }

        return AccountCatalogueUpdateRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .financialStatus(accountCatalogue.getFinancialStatus().getState())
                .nature(accountCatalogue.getNature().getState())
                .classification(accountCatalogue.getClassification().getState())
                .parent(accountCatalogue.getParent().getCode() == null ? null : accountCatalogue.getParent().getCode())
                .build();
    }
    
}
