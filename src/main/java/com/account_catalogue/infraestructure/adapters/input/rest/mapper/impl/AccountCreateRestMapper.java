package com.account_catalogue.infraestructure.adapters.input.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;


@Component
public class AccountCreateRestMapper implements IAccountCreateRestMapper {


    @Override
    public AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue) {
        return AccountCatalogueCreateRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .financialStatus(accountCatalogue.getFinancialStatus())
                .nature(accountCatalogue.getNature())
                .classification(accountCatalogue.getClassification())
                .build();
    }

    @Override
    public AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq, AccountCatalogue padre) { 
        if(accountCatalogueCreateReq==null){
            return null;
        }

        AccountCatalogue accountCatalogue=AccountCatalogue.builder()
                .code(accountCatalogueCreateReq.getCode())
                .description(accountCatalogueCreateReq.getDescription())
                .financialStatus(accountCatalogueCreateReq.getFinancialStatus())
                .nature(accountCatalogueCreateReq.getNature())
                .classification(accountCatalogueCreateReq.getClassification())
                .parent(padre)
                .build();

        padre = accountCatalogue;     

       
        List<AccountCatalogue> children = new ArrayList<>();
        for (AccountCatalogueCreateReq child : accountCatalogueCreateReq.getChildren()) {
            AccountCatalogue childAccountCatalogue = toDomain(child, padre);
            if (childAccountCatalogue != null) {
                children.add(childAccountCatalogue);
            }
        }
        accountCatalogue.setChildren(children);

        return accountCatalogue;
    } 
}
