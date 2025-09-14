package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long originalReceiptId;

    @Column(nullable = false, length = 50)
    private String receiptCode;

    @Column(nullable = false)
    private Long thirdPartyId;

    @Column(nullable = false)
    private Long paymentMethodId;
    
    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(length = 255)
    private String observations;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptDetailEntity> details;

    
}
