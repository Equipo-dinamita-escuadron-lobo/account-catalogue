package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ITaxSearchRestMapper {
    default TaxSearchRes toSearchResponse(Tax tax){
        if(tax==null){
            return null;
        }
        return TaxSearchRes.builder()
                .id(tax.getId())
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(tax.getRefundAccount().getCode())
                .depositAccount(tax.getDepositAccount().getCode())
                .build();
    }
    default List<TaxSearchRes> toSearchListResponse(List<Tax> taxes){
        if(taxes==null){
            return null;
        }
        return  taxes.stream()
                .map(tax -> {
                        TaxSearchRes taxSearchRes = TaxSearchRes.builder()
                            .id(tax.getId())
                            .code(tax.getCode())
                            .description(tax.getDescription())
                            .interest(tax.getInterest())
                            .depositAccount(tax.getDepositAccount().getCode())
                            .refundAccount(tax.getRefundAccount().getCode())
                            .build();
                    return taxSearchRes;

                })
                .toList();

    }
}
