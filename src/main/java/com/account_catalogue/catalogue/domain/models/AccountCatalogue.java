package com.account_catalogue.catalogue.domain.models;

import java.math.BigDecimal;
import java.util.List;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief Modelo de dominio para cuentas contables del catálogo
 *
 * Representa una cuenta contable completa con toda su información jerárquica,
 * asociaciones con impuestos y campos opcionales para funcionalidad avanzada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountCatalogue {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private NatureEnum nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;
    private AccountCatalogue  parent;
    private List<AccountCatalogue> children;
    private List<TaxEntity> salesTaxes;
    private List<TaxEntity>  purchaseTaxes;
    private Boolean crossing;
    private Boolean costCenter;
    private Boolean status;
    private Boolean isDeleted;
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;
    @Builder.Default
    private Integer usageCount = 0;

    /**
     * @brief Incrementa el contador de uso de la cuenta contable
     */
    public void incrementUsageCount() {
        this.usageCount = this.usageCount == null ? 1 : this.usageCount + 1;
    }

    /**
     * @brief Verifica si la cuenta contable está siendo usada
     * @return true si la cuenta tiene uso registrado
     */
    public boolean isInUse() {
        return this.usageCount != null && this.usageCount > 0;
    }
}
