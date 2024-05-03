package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import java.util.List;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

}
