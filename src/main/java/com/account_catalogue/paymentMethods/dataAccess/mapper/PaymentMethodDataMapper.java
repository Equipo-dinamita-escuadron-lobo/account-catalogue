package com.account_catalogue.paymentMethods.dataAccess.mapper;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMethodDataMapper {

    @Mapping(target = "accountingAccount", expression = "java(entity.getAccountingAccount() != null ? entity.getAccountingAccount().getCode() + \" - \" + entity.getAccountingAccount().getDescription() : \"\")")
    @Mapping(target = "accountingAccountEntity", expression = "java(mapToAccountCatalogue(entity.getAccountingAccount()))")
    PaymentMethod toDomain(PaymentMethodEntity entity);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "accountingAccount", ignore = true)
    PaymentMethodEntity toEntity(PaymentMethod domain);

    @AfterMapping
    default void setAccountingAccountEntity(PaymentMethod domain, @MappingTarget PaymentMethodEntity entity) {
        // La relación se maneja por separado, no en el mapeo automático
    }

    default AccountCatalogue mapToAccountCatalogue(AccountCatalogueEntity entity) {
        if (entity == null) {
            return null;
        }
        return AccountCatalogue.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .build();
    }
}
