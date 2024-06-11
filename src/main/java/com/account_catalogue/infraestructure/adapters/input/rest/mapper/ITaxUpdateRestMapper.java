package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxUpdateRes;
import org.mapstruct.Mapper;

@Mapper
public interface ITaxUpdateRestMapper {
    TaxDTO toDomain(TaxUpdateReq taxUpdateReq);
    default TaxUpdateRes toCreateResponse(Tax tax){
        if(tax==null){
            return null;
        }
        return TaxUpdateRes.builder()
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(tax.getRefundAccount().getCode())
                .depositAccount(tax.getDepositAccount().getCode())
                .build();
    }
}
