package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxUpdateRes;
import org.mapstruct.Mapper;

@Mapper
public interface ITaxUpdateRestMapper {
    default TaxDTO toDomain(TaxUpdateReq taxUpdateReq){
        if(taxUpdateReq==null){
            return null;
        }
        return TaxDTO.builder()
                .idEnterprise(taxUpdateReq.getIdEnterprise())
                .code(taxUpdateReq.getCode())
                .description(taxUpdateReq.getDescription())
                .interest(taxUpdateReq.getInterest())
                .depositAccount(taxUpdateReq.getDepositAccount())
                .refundAccount(taxUpdateReq.getRefundAccount())
                .build();

    }
    default TaxUpdateRes toCreateResponse(Tax tax){
        if(tax==null){
            return null;
        }
        return TaxUpdateRes.builder()
                .id(tax.getId())
                .idEnterprise(tax.getIdEnterprise())
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(tax.getRefundAccount().getCode())
                .depositAccount(tax.getDepositAccount().getCode())
                .build();
    }
}
