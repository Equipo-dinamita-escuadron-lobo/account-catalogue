package com.chartaccounts.infraestructure.adapters.output.jpaAdapter.entity;

import jakarta.persistence.*;


@Entity
@Table(name="Account")
public class AccountCatalogueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String description;

    private String  nature;
    private String financialStatus;
    private String classification;

}
