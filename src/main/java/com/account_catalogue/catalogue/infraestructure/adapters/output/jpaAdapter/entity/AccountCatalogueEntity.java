package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.TenantId;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(
    name="Account",
    indexes = {
        @Index(name = "idx_account_id_enterprise", columnList = "idEnterprise"),
        @Index(name = "idx_account_code", columnList = "code"),
        @Index(name = "idx_account_status", columnList = "status"),
        @Index(name = "idx_account_enterprise_status", columnList = "idEnterprise, status")
    }
)
public class AccountCatalogueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String description;

    private NatureEnum  nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private AccountCatalogueEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountCatalogueEntity> children;

   @OneToMany(mappedBy = "salesTax", fetch = FetchType.LAZY)
   private List<TaxEntity> salesTaxes;

   @OneToMany(mappedBy = "purchaseTax", fetch = FetchType.LAZY)
   private List<TaxEntity>  purchaseTaxes;

    private String idEnterprise;

    @TenantId
    String tenantId;

    private Boolean crossing;
    private Boolean costCenter;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @Formula("LENGTH(code)")
    private Integer codeLength;
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

}
