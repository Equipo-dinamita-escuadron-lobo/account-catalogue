package com.account_catalogue.infraestructure.adapters.input.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import com.account_catalogue.infraestructure.adapters.input.rest.util.AdjustEnumAccount;
import org.springframework.stereotype.Component;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;


@Component
public class AccountCreateRestMapper implements IAccountCreateRestMapper {

    private AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
    
    @Override
    public AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogueRes) {
        if(accountCatalogueRes==null){
            return null;
        }

        AccountCatalogueCreateRes accountCatalogue=AccountCatalogueCreateRes.builder()
                .id(accountCatalogueRes.getId())
                .code(accountCatalogueRes.getCode())
                .description(accountCatalogueRes.getDescription())
                .financialStatus(accountCatalogueRes.getFinancialStatus())
                .nature(accountCatalogueRes.getNature())
                .classification(accountCatalogueRes.getClassification())
                .build();
       
        return accountCatalogue;   
    }

    @Override
    public AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq, AccountCatalogue padre) { 
        if(accountCatalogueCreateReq==null){
            return null;
        }

        AccountCatalogue accountCatalogue=AccountCatalogue.builder()
                .code(accountCatalogueCreateReq.getCode())
                .description(accountCatalogueCreateReq.getDescription())
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueCreateReq.getFinancialStatus()))
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueCreateReq.getNature()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueCreateReq.getClassification()))
                .parent(padre)
                .build();
                
        if(accountCatalogueCreateReq.getChildren()==null){
            return accountCatalogue;
        }

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
