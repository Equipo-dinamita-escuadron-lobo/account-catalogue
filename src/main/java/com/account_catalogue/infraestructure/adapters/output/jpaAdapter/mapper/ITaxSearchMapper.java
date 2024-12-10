package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ITaxSearchMapper {
    /**
     * Mapea un objeto TaxEntity a un objeto Tax.
     *
     * @param taxEntity el objeto TaxEntity a mapear
     * @return el objeto Tax mapeado, o null si el objeto TaxEntity es null
     */
    default Tax toDomain(TaxEntity taxEntity) {
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
                .build();
    }

    /**
     * Mapea una lista de objetos TaxEntity a una lista de objetos Tax.
     *
     * @param taxes la lista de objetos TaxEntity a mapear
     * @return una lista de objetos Tax mapeados, o null si la lista de entrada es
     *         null
     */
    default List<Tax> toDomainList(List<TaxEntity> taxes) {
        if (taxes == null) {
            return null;
        }
        return taxes.stream()
                .map(taxEntity -> {
                    Tax tax = Tax.builder()
                            .id(taxEntity.getId())
                            .code(taxEntity.getCode())
                            .idEnterprise(taxEntity.getIdEnterprise())
                            .description(taxEntity.getDescription())
                            .interest(taxEntity.getInterest())
                            .depositAccount(taxEntity.getDepositAccount())
                            .refundAccount(taxEntity.getRefundAccount())
                            .build();
                    return tax;

                })
                .toList();
    }
}
