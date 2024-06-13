package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import org.mapstruct.Mapper;



@Mapper
public interface ITaxCreateRestMapper {

    default TaxDTO toDomain(TaxCreateReq taxCreateReq){
        if(taxCreateReq==null){
            return null;
        }
        return TaxDTO.builder()
                .idEnterprise(taxCreateReq.getIdEnterprise())
                .code(taxCreateReq.getCode())
                .description(taxCreateReq.getDescription())
                .interest(taxCreateReq.getInterest())
                .refundAccount(taxCreateReq.getRefundAccount())
                .depositAccount(taxCreateReq.getDepositAccount())
                .build();
    }
    default TaxCreateRes toCreateResponse(Tax tax){
        if(tax==null){
            return null;
        }
        return TaxCreateRes.builder()
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
