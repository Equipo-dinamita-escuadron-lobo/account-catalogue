package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import java.util.List;
import java.util.Set;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;

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

    @Column(unique = true)
    private String code;

    private String description;

    private NatureEnum  nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "code") 
    private AccountCatalogueEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AccountCatalogueEntity> children;

   @OneToMany(mappedBy = "account")
    private Set<AccountTaxEntity> accountTaxes;

    private String idEnterprise;

}
