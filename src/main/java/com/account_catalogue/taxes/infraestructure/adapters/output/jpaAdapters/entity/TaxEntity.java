package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TenantId;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;


@Entity
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name="Tax", 
    indexes = {
        @Index(name = "idx_tax_id_enterprise", columnList = "idEnterprise"),
        @Index(name = "idx_tax_code", columnList = "code"),
        @Index(name = "idx_tax_status", columnList = "status"),
        @Index(name = "idx_tax_enterprise_status", columnList = "idEnterprise, status")
    })
public class TaxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    private String description;
    
    @Positive
    @Column(nullable = false)
    private Double interest;

    @ManyToOne
    @JoinColumn(name="salesTax_code")
    private AccountCatalogueEntity salesTax;

    @ManyToOne
    @JoinColumn(name="purchaseTax_code")
    private AccountCatalogueEntity purchaseTax;

    private String idEnterprise;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @TenantId
    String tenantId;
}
