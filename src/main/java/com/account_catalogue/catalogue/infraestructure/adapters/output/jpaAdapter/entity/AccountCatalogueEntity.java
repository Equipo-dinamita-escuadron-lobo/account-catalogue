package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import java.util.List;
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
@Table(name="Account")
public class AccountCatalogueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String description;

    private NatureEnum  nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id") 
    private AccountCatalogueEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AccountCatalogueEntity> children;

   @OneToMany(mappedBy = "depositAccount")
   private List<TaxEntity> depositAccounts;

   @OneToMany(mappedBy = "refundAccount")
   private List<TaxEntity>  refundAccounts;




    private String idEnterprise;

    @TenantId
    String tenantId;

    private Boolean crossing;
    private Boolean costCenter;

}
