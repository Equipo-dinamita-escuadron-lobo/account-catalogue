package com.account_catalogue.paymentMethods.dataAccess.mapper;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodDataMapper {
    
    PaymentMethod toDomain(PaymentMethodEntity entity);
    
    @Mapping(target = "tenantId", ignore = true)
    PaymentMethodEntity toEntity(PaymentMethod domain);
}
