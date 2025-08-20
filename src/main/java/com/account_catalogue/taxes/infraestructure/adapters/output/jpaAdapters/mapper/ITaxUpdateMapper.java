package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper;


import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import org.mapstruct.Mapper;

@Mapper
public interface ITaxUpdateMapper {
    /**
     * Mapea un objeto Tax a un objeto TaxEntity.
     *
     * @param tax el objeto Tax a mapear
     * @return el objeto TaxEntity mapeado, o null si el objeto Tax es null
     */
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
    /**
     * Mapea un objeto TaxEntity a un objeto Tax.
     *
     * @param taxEntity el TaxEntity a mapear
     * @return el modelo de dominio Tax mapeado, o null si el TaxEntity es null
     */
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
