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

@Table(name="Tax")
public class TaxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;
    private String description;
    private float interest;

    @ManyToOne
    @JoinColumn(name="depositAccount_code")
    private  AccountCatalogueEntity depositAccount;

    @ManyToOne
    @JoinColumn(name="refundAccount_code")
    private  AccountCatalogueEntity refundAccount;








    
}
