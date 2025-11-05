package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AuxiliaryAccountListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAuxiliaryAccountRestMapper;

@Component
public class AuxiliaryAccountRestMapper implements IAuxiliaryAccountRestMapper {

    /**
     * Transforma una lista de AccountCatalogue en AuxiliaryAccountListRes.
     * 
     * @param auxiliaryAccounts lista de cuentas auxiliares del dominio
     * @param idEnterprise el ID de la empresa
     * @return el objeto AuxiliaryAccountListRes con la lista transformada
     */
    @Override
    public AuxiliaryAccountListRes toAuxiliaryAccountListRes(List<AccountCatalogue> auxiliaryAccounts, String idEnterprise) {
        if (auxiliaryAccounts == null) {
            return AuxiliaryAccountListRes.builder()
                    .auxiliaryAccounts(List.of())
                    .totalCount(0)
                    .idEnterprise(idEnterprise)
                    .build();
        }
        
        List<ItemAccountCatalogueSearchRes> accountList = auxiliaryAccounts.stream()
                .map(this::toItemAccountCatalogueSearchRes)
                .collect(Collectors.toList());
        
        return AuxiliaryAccountListRes.builder()
                .auxiliaryAccounts(accountList)
                .totalCount(accountList.size())
                .idEnterprise(idEnterprise)
                .build();
    }

    /**
     * Transforma un AccountCatalogue en ItemAccountCatalogueSearchRes.
     * 
     * @param accountCatalogue el objeto AccountCatalogue a transformar
     * @return el objeto ItemAccountCatalogueSearchRes transformado
     */
    @Override
    public ItemAccountCatalogueSearchRes toItemAccountCatalogueSearchRes(AccountCatalogue accountCatalogue) {
        if (accountCatalogue == null) {
            return null;
        }
        
        return ItemAccountCatalogueSearchRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature() != null ? accountCatalogue.getNature().getState() : null)
                .financialStatus(accountCatalogue.getFinancialStatus() != null ? accountCatalogue.getFinancialStatus().getState() : null)
                .classification(accountCatalogue.getClassification() != null ? accountCatalogue.getClassification().getState() : null)
                .crossing(accountCatalogue.getCrossing())
                .costCenter(accountCatalogue.getCostCenter())
                .status(accountCatalogue.getStatus())
                .parent(map(accountCatalogue.getParent())) // Usa el método de mapeo definido en la interfaz
                .build();
    }
}
