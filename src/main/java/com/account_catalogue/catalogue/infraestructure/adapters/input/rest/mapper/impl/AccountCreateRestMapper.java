package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.util.AdjustEnumAccount;

@Component
public class AccountCreateRestMapper implements IAccountCreateRestMapper {

    private AdjustEnumAccount adjustEnum = new AdjustEnumAccount();

    /**
     * Convierte un modelo de dominio AccountCatalogue en un objeto de respuesta
     * AccountCatalogueCreateRes para comunicación REST.
     *
     * @param accountCatalogueRes El modelo de dominio AccountCatalogue a convertir.
     *                            Si es null, el método devuelve null.
     * @return Un objeto AccountCatalogueCreateRes que contiene los detalles del
     *         AccountCatalogue proporcionado, o null si la entrada es null.
     */
    @Override
    public AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogueRes) {
        if (accountCatalogueRes == null) {
            return null;
        }

        AccountCatalogueCreateRes accountCatalogue = AccountCatalogueCreateRes.builder()
                .idEnterprise(accountCatalogueRes.getIdEnterprise())
                .id(accountCatalogueRes.getId())
                .code(accountCatalogueRes.getCode())
                .description(accountCatalogueRes.getDescription())
                .financialStatus(accountCatalogueRes.getFinancialStatus() != null ? accountCatalogueRes.getFinancialStatus().getState() : null)
                .nature(accountCatalogueRes.getNature() != null ? accountCatalogueRes.getNature().getState() : null)
                .classification(accountCatalogueRes.getClassification() != null ? accountCatalogueRes.getClassification().getState() : null)
                .crossing(accountCatalogueRes.getCrossing())
                .costCenter(accountCatalogueRes.getCostCenter())
                .status(accountCatalogueRes.getStatus())
                .parent(accountCatalogueRes.getParent() != null ? accountCatalogueRes.getParent().getCode() : null)
                .build();

        return accountCatalogue;
    }

    /**
     * Convierte un objeto AccountCatalogueCreateReq en un objeto de modelo de
     * dominio
     * AccountCatalogue.
     *
     * @param accountCatalogueCreateReq El objeto AccountCatalogueCreateReq a
     *                                  convertir.
     *                                  Si es null, el método devuelve null.
     * @param padre                     El objeto de modelo de dominio
     *                                  AccountCatalogue
     *                                  padre, si corresponde.
     * @return Un objeto de modelo de dominio AccountCatalogue que contiene los
     *         detalles
     *         del AccountCatalogueCreateReq proporcionado, o null si la entrada es
     *         null.
     */
    @Override
    public AccountCatalogue toDomain(AccountCatalogueCreateReq accountCatalogueCreateReq, AccountCatalogue padre) {
        if (accountCatalogueCreateReq == null) {
            return null;
        }

        AccountCatalogue accountCatalogue = AccountCatalogue.builder()
                .idEnterprise(accountCatalogueCreateReq.getIdEnterprise())
                .code(accountCatalogueCreateReq.getCode())
                .description(accountCatalogueCreateReq.getDescription())
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueCreateReq.getFinancialStatus()))
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueCreateReq.getNature()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueCreateReq.getClassification()))
                .crossing(accountCatalogueCreateReq.getCrossing())
                .costCenter(accountCatalogueCreateReq.getCostCenter())
                .parent(padre)
                .build();

        if (accountCatalogueCreateReq.getChildren() == null) {
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
