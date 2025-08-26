package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity;

import jakarta.persistence.*;
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
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "idEnterprise"}),
    indexes = {
        @Index(name = "idx_tax_id_enterprise", columnList = "idEnterprise"),
        @Index(name = "idx_tax_code", columnList = "code"),
        @Index(name = "idx_tax_status", columnList = "status"),
        @Index(name = "idx_tax_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_tax_enterprise_deleted", columnList = "idEnterprise, is_deleted"),
        @Index(name = "idx_tax_enterprise_status", columnList = "idEnterprise, status")
    })
public class TaxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    private String description;
    private float interest;

    @ManyToOne
    @JoinColumn(name="depositAccount_code")
    private AccountCatalogueEntity depositAccount;

    @ManyToOne
    @JoinColumn(name="refundAccount_code")
    private AccountCatalogueEntity refundAccount;

    private String idEnterprise;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @TenantId
    String tenantId;
}
