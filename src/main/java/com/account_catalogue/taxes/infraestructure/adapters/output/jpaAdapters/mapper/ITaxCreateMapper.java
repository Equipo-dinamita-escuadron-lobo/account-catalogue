package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper;

import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import org.mapstruct.Mapper;

/**
 * @brief Mapeador de datos para operaciones de creación de impuestos
 *
 * Gestiona la conversión entre entidades JPA y modelos de dominio
 * para operaciones de persistencia de nuevos impuestos.
 */
@Mapper
public interface ITaxCreateMapper {
    /**
     * @brief Convierte modelo de dominio a entidad JPA
     * @param tax modelo de dominio con entidades relacionadas cargadas
     * @return entidad preparada para persistencia con status por defecto
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
                .salesTax(tax.getSalesTax())
                .purchaseTax(tax.getPurchaseTax())
                .status(tax.getStatus() != null ? tax.getStatus() : true)
                .build();
    }

    /**
     * @brief Convierte entidad JPA a modelo de dominio
     * @param taxEntity entidad con relaciones JPA cargadas
     * @return modelo de dominio con entidades relacionadas mapeadas
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
                .purchaseTax(taxEntity.getPurchaseTax())
                .salesTax(taxEntity.getSalesTax())
                .status(taxEntity.getStatus())
                .build();
    }
}
