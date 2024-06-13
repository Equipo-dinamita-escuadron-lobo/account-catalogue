package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;


import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ITaxUpdateMapper {
    default TaxEntity toEntity(Tax tax){
        if(tax==null){
            return null;

        }


        return TaxEntity.builder()
                .code(tax.getCode())
                .idEnterprise(tax.getIdEnterprise())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .depositAccount(tax.getDepositAccount())
                .refundAccount(tax.getRefundAccount())
                .build();
    }
    default Tax toModel(TaxEntity taxEntity){
        if(taxEntity==null){
            return null;
        }

        return Tax.builder()
                .id(taxEntity.getId())
                .code(taxEntity.getCode())
                .idEnterprise(taxEntity.getIdEnterprise())
                .description(taxEntity.getDescription())
                .interest(taxEntity.getInterest())
                .refundAccount(taxEntity.getRefundAccount())
                .depositAccount(taxEntity.getDepositAccount())
                .build();
    }
}
