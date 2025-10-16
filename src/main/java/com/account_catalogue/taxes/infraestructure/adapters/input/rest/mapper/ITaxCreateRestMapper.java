package com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response.TaxCreateRes;

import org.mapstruct.Mapper;

@Mapper
public interface ITaxCreateRestMapper {

    /**
     * Este método toma un objeto TaxCreateReq y devuelve un objeto TaxDTO.
     * Es una simple mapeo del request al DTO.
     *
     * @param taxCreateReq el request a mapear
     * @return el DTO mapeado, o null si el request es null
     */
    default TaxDTO toDomain(TaxCreateReq taxCreateReq) {
        if (taxCreateReq == null) {
            return null;
        }
        return TaxDTO.builder()
                .idEnterprise(taxCreateReq.getIdEnterprise())
                .code(taxCreateReq.getCode())
                .description(taxCreateReq.getDescription())
                .interest(taxCreateReq.getInterest())
                .refundAccountId(taxCreateReq.getRefundAccountId())
                .depositAccountId(taxCreateReq.getDepositAccountId())
                .build();
    }

    /**
     * Este método toma un objeto Tax y devuelve un objeto TaxCreateRes.
     * Es una simple mapeo del Tax a la respuesta.
     *
     * @param tax el Tax a mapear
     * @return la respuesta mapeada, o null si el Tax es null
     */
    default TaxCreateRes toCreateResponse(Tax tax) {
        if (tax == null) {
            return null;
        }
        return TaxCreateRes.builder()
                .id(tax.getId())
                .idEnterprise(tax.getIdEnterprise())
                .code(tax.getCode())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccountId(tax.getRefundAccount() != null ? tax.getRefundAccount().getId() : null)
                .depositAccountId(tax.getDepositAccount() != null ? tax.getDepositAccount().getId() : null)
                .status(tax.getStatus())
                .build();
    }
}
