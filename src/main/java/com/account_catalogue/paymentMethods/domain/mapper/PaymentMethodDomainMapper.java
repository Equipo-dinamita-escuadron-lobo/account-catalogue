package com.account_catalogue.paymentMethods.domain.mapper;

import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodCreateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.response.PaymentMethodRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodDomainMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    PaymentMethod toDomain(PaymentMethodCreateReq request);
    
    @Mapping(target = "isDeleted", ignore = true)
    PaymentMethod toDomain(PaymentMethodUpdateReq request);
    
    PaymentMethodRes toRes(PaymentMethod domain);
}
