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
@Table(name="Tax", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "idEnterprise"}))
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

    @TenantId
    String tenantId;
}
