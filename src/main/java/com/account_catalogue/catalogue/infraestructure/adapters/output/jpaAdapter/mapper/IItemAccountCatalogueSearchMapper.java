package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IItemAccountCatalogueSearchMapper {

    /**
     * Convierte un objeto AccountCatalogueEntity en un objeto AccountCatalogue.
     * 
     * @param accountCatalogueEntity el objeto AccountCatalogueEntity a convertir.
     *                               Si es null, el m todo devuelve null.
     * @return un objeto AccountCatalogue que contiene los detalles del
     *         AccountCatalogueEntity proporcionado, o null si la entrada es
     *         null.
     */
    default AccountCatalogue toDomain(AccountCatalogueEntity accountCatalogueEntity) {
        if (accountCatalogueEntity == null) {
            return null;
        }
        return AccountCatalogue.builder()
                .id(accountCatalogueEntity.getId())
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .nature(accountCatalogueEntity.getNature())
                .financialStatus(accountCatalogueEntity.getFinancialStatus())
                .classification(accountCatalogueEntity.getClassification())
                .crossing(accountCatalogueEntity.getCrossing())
                .costCenter(accountCatalogueEntity.getCostCenter())
                .isDeleted(accountCatalogueEntity.getIsDeleted())
                .parent(auxParent(
                        accountCatalogueEntity.getParent() == null ? null : accountCatalogueEntity.getParent()))
                .build();
    }

    /**
     * Convierte un objeto AccountCatalogue en un objeto AccountCatalogueEntity.
     * 
     * @param accountCatalogue el objeto AccountCatalogue a convertir.
     * @return un objeto AccountCatalogueEntity que representa el objeto
     *         AccountCatalogue proporcionado.
     *         El campo tenantId se ignora durante el mapeo.
     */
    @Mapping(target = "tenantId", ignore = true)
    AccountCatalogueEntity toEntity(AccountCatalogue accountCatalogue);

    default AccountCatalogue toDomainTree(AccountCatalogueEntity accountCatalogueEntity) {
        if (accountCatalogueEntity == null) {
            return null;
        }

        AccountCatalogue accountCatalogue = AccountCatalogue.builder()
                .id(accountCatalogueEntity.getId())
                .code(accountCatalogueEntity.getCode())
                .description(accountCatalogueEntity.getDescription())
                .nature(accountCatalogueEntity.getNature())
                .financialStatus(accountCatalogueEntity.getFinancialStatus())
                .classification(accountCatalogueEntity.getClassification())
                .crossing(accountCatalogueEntity.getCrossing())
                .costCenter(accountCatalogueEntity.getCostCenter())
                .isDeleted(accountCatalogueEntity.getIsDeleted())
                .parent(auxParent(
                        accountCatalogueEntity.getParent() == null ? null : accountCatalogueEntity.getParent()))
                .build();

        List<AccountCatalogue> children = new ArrayList<>();
        for (AccountCatalogueEntity child : accountCatalogueEntity.getChildren()) {
            AccountCatalogue childAccountCatalogue = toDomainTree(child);
            if (childAccountCatalogue != null) {
                children.add(childAccountCatalogue);
            }
        }
        accountCatalogue.setChildren(children);

        return accountCatalogue;
    }

    /**
     * Convierte un objeto AccountCatalogueEntity en un objeto AccountCatalogue con
     * los campos id y code.
     * 
     * @param accountCatalogue el objeto AccountCatalogueEntity a convertir.
     *                         Si es null, el m todo devuelve un objeto
     *                         AccountCatalogue con los campos id y code
     *                         establecidos en null.
     * @return un objeto AccountCatalogue que contiene los campos id y code del
     *         AccountCatalogueEntity proporcionado, o un objeto con los campos
     *         id y code establecidos en null si la entrada es null.
     */
    default AccountCatalogue auxParent(AccountCatalogueEntity accountCatalogue) {
        if (accountCatalogue == null) {
            AccountCatalogue accountCatalogueNull = AccountCatalogue.builder()
                    .id(null)
                    .code(null)
                    .build();

            return accountCatalogueNull;
        }

        AccountCatalogue accountCatalogueParent = AccountCatalogue.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .build();

        return accountCatalogueParent;
    }

}
