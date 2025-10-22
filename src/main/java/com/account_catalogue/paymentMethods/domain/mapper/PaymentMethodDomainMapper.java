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
    @Mapping(target = "accountingAccountEntity", ignore = true)
    @Mapping(target = "accountingAccount", expression = "java(\"\" + request.getAccountingAccountId())") // Convertir ID a String para compatibilidad
    PaymentMethod toDomain(PaymentMethodCreateReq request);

    @Mapping(target = "accountingAccountEntity", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accountingAccount", expression = "java(\"\" + request.getAccountingAccountId())") // Convertir ID a String para compatibilidad
    PaymentMethod toDomain(PaymentMethodUpdateReq request);

    @Mapping(target = "accountingAccount", source = "accountingAccount")
    @Mapping(target = "accountingAccountId", expression = "java(domain.getAccountingAccountEntity() != null ? domain.getAccountingAccountEntity().getId() : null)")
    PaymentMethodRes toRes(PaymentMethod domain);


}
