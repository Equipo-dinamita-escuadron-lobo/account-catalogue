package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.mapstruct.Mapper;


@Mapper
public interface ITaxCreateMapper {
    default TaxEntity toEntity(Tax tax){
        if(tax==null){
            return null;

        }
        return TaxEntity.builder()
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(tax.getRefundAccount())
                .account(tax.getAccount())
                .build();
    }
    default Tax toModel(TaxEntity taxEntity){
        if(taxEntity==null){
            return null;
        }
        return Tax.builder()
                .code(taxEntity.getCode())
                .description(taxEntity.getDescription())
                .interest(taxEntity.getInterest())
                .refundAccount(taxEntity.getRefundAccount())
                .account(taxEntity.getAccount())
                .build();
    }
}
