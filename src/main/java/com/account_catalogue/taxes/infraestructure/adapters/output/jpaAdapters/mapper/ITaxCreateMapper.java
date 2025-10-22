package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper;

import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import org.mapstruct.Mapper;

@Mapper
public interface ITaxCreateMapper {
    /**
     * Este metodo toma un objeto Tax y devuelve un objeto TaxEntity.
     * Es una simple mapeo del Tax al TaxEntity.
     *
     * @param tax el objeto Tax a mapear
     * @return el TaxEntity mapeado, o null si el objeto Tax es null
     */
    default TaxEntity toEntity(Tax tax) {
        if (tax == null) {
            return null;

        }

        return TaxEntity.builder()
                .id(tax.getId())
                .idEnterprise(tax.getIdEnterprise())
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .depositAccount(tax.getDepositAccount())
                .refundAccount(tax.getRefundAccount())
                .status(tax.getStatus() != null ? tax.getStatus() : true)
                .build();
    }

    /**
     * Este método toma un objeto TaxEntity y devuelve un objeto Tax.
     * Es una simple mapeo del TaxEntity al modelo de dominio Tax.
     *
     * @param taxEntity el TaxEntity a mapear
     * @return el modelo de dominio Tax mapeado, o null si el TaxEntity es null
     */
    default Tax toModel(TaxEntity taxEntity) {
        if (taxEntity == null) {
            return null;
        }

        return Tax.builder()
                .id(taxEntity.getId())
                .idEnterprise(taxEntity.getIdEnterprise())
                .code(taxEntity.getCode())
                .description(taxEntity.getDescription())
                .interest(taxEntity.getInterest())
                .refundAccount(taxEntity.getRefundAccount())
                .depositAccount(taxEntity.getDepositAccount())
                .status(taxEntity.getStatus())
                .build();
    }
}
