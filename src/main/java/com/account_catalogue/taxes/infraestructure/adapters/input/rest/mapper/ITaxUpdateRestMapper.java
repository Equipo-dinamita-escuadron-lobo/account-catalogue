package com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.dto.request.TaxUpdateReq;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.dto.response.TaxUpdateRes;

import org.mapstruct.Mapper;

@Mapper
public interface ITaxUpdateRestMapper {
    /**
     * Este método toma un objeto TaxUpdateReq y devuelve un objeto TaxDTO.
     * Es una simple mapeo del request al DTO.
     *
     * @param taxUpdateReq el request a mapear
     * @return el DTO mapeado, o null si el request es null
     */
    default TaxDTO toDomain(TaxUpdateReq taxUpdateReq){
        if(taxUpdateReq==null){
            return null;
        }
        return TaxDTO.builder()
                .idEnterprise(taxUpdateReq.getIdEnterprise())
                .code(taxUpdateReq.getCode())
                .description(taxUpdateReq.getDescription())
                .interest(taxUpdateReq.getInterest())
                .salesTaxId(taxUpdateReq.getSalesTaxId())
                .purchaseTaxId(taxUpdateReq.getPurchaseTaxId())
                .build();

    }
    /**
     * Mapea un objeto Tax a un objeto TaxUpdateRes.
     *
     * @param tax el objeto Tax a mapear
     * @return un objeto TaxUpdateRes con la información mapeada, o null si el objeto Tax es null
     */
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
                .purchaseTax(tax.getPurchaseTax() != null ? tax.getPurchaseTax().getId() : null)
                .salesTax(tax.getSalesTax() != null ? tax.getSalesTax().getId() : null)
                .status(tax.getStatus())
                .build();
    }
}
