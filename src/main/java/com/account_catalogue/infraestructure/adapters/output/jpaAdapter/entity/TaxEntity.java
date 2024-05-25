package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name="Tax")
public class TaxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;
    private String description;
    private float interest;
    private String refundAccount;
    private String account;
    @OneToMany(mappedBy = "tax")
    private Set<AccountTaxEntity> accountImpuesto;




    
}
