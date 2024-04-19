package com.chartaccounts.infraestructure.adapters.output.jpaAdapter.entity;

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
    private long id;

    @Column(unique = true)
    private String code;

    private String description;

    private String  nature;
    private String financialStatus;
    private String classification;

}
