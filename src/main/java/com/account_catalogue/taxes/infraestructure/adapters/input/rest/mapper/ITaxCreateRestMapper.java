package com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.dto.request.TaxCreateReq;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.dto.response.TaxCreateRes;

import org.mapstruct.Mapper;

/**
 * @brief Mapeador REST para operaciones de creación de impuestos
 *
 * Gestiona la conversión entre DTOs de presentación y modelos de dominio
 * para operaciones de creación de impuestos.
 */
@Mapper
public interface ITaxCreateRestMapper {

    /**
     * @brief Convierte request de creación a DTO de dominio
     * @param taxCreateReq request de creación de impuesto
     * @return DTO de dominio con datos mapeados
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
                .purchaseTaxId(taxCreateReq.getPurchaseTaxId())
                .salesTaxId(taxCreateReq.getSalesTaxId())
                .build();
    }

    /**
     * @brief Convierte modelo de dominio a response de creación
     * @param tax modelo de dominio con entidades relacionadas cargadas
     * @return response con IDs de cuentas contables extraídos
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
                .purchaseTaxId(tax.getPurchaseTax() != null ? tax.getPurchaseTax().getId() : null)
                .salesTaxId(tax.getSalesTax() != null ? tax.getSalesTax().getId() : null)
                .status(tax.getStatus())
                .usageCount(tax.getUsageCount())
                .build();
    }
}
