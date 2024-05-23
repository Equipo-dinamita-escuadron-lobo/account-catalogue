package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity;

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
@Table(name="AccountTax")
public class AccountTaxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "codeAccount")
    private AccountCatalogueEntity account;
    @ManyToOne
    @JoinColumn(name = "codeTax")
    private TaxEntity  tax;


}
