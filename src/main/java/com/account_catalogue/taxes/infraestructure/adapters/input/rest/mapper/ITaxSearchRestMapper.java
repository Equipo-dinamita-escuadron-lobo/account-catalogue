package com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response.TaxSearchRes;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ITaxSearchRestMapper {
    /**
     * Mapea un objeto de dominio Tax a un objeto de respuesta TaxSearchRes.
     *
     * @param tax el objeto de dominio Tax a mapear
     * @return un objeto de respuesta TaxSearchRes con la información mapeada, o
     *         null si el objeto Tax de entrada es null
     */
    default TaxSearchRes toSearchResponse(Tax tax) {
        if (tax == null) {
            return null;
        }
        return TaxSearchRes.builder()
                .id(tax.getId())
                .idEnterprise(tax.getIdEnterprise())
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(tax.getRefundAccount().getCode())
                .depositAccount(tax.getDepositAccount().getCode())
                .build();
    }

    /**
     * Mapea una lista de objetos de dominio Tax a una lista de objetos de respuesta
     * TaxSearchRes.
     *
     * @param taxes la lista de objetos de dominio Tax a mapear
     * @return una lista de objetos de respuesta TaxSearchRes con la información
     *         mapeada, o null si la lista de entrada es null
     */
    default List<TaxSearchRes> toSearchListResponse(List<Tax> taxes) {
        if (taxes == null) {
            return null;
        }
        return taxes.stream()
                .map(tax -> {
                    TaxSearchRes taxSearchRes = TaxSearchRes.builder()
                            .id(tax.getId())
                            .idEnterprise(tax.getIdEnterprise())
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
