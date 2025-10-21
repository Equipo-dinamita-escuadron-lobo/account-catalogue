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

        AccountCatalogue.AccountCatalogueBuilder builder = AccountCatalogue.builder()
                .idEnterprise(accountCatalogueUpdateReq.getIdEnterprise())
                .code(accountCatalogueUpdateReq.getCode())
                .description(accountCatalogueUpdateReq.getDescription())
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueUpdateReq.getFinancialStatus()))
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueUpdateReq.getNature()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueUpdateReq.getClassification()))
                .crossing(accountCatalogueUpdateReq.getCrossing())
                .costCenter(accountCatalogueUpdateReq.getCostCenter());

        // Manejar el parent si se proporciona
        if (accountCatalogueUpdateReq.getParent() != null) {
            AccountCatalogue parent = AccountCatalogue.builder()
                    .id(accountCatalogueUpdateReq.getParent())
                    .build();
            builder.parent(parent);
        }

        return builder.build();
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
                .financialStatus(accountCatalogue.getFinancialStatus() != null ? accountCatalogue.getFinancialStatus().getState() : null)
                .nature(accountCatalogue.getNature() != null ? accountCatalogue.getNature().getState() : null)
                .classification(accountCatalogue.getClassification() != null ? accountCatalogue.getClassification().getState() : null)
                .crossing(accountCatalogue.getCrossing())
                .costCenter(accountCatalogue.getCostCenter())
                .status(accountCatalogue.getStatus())
                .parent(accountCatalogue.getParent() != null ? accountCatalogue.getParent().getCode() : null)
                .build();
    }
    
}
