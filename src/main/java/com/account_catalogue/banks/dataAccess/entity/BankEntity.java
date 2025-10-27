package com.account_catalogue.banks.dataAccess.entity;

import com.account_catalogue.banks.domain.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.util.Set;

@Entity
@Table(name = "banks", indexes = {
        @Index(name = "idx_bank_id_enterprise", columnList = "id_enterprise"),
        @Index(name = "idx_bank_code", columnList = "code"),
        @Index(name = "idx_bank_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 2)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection(targetClass = Currency.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "bank_currencies", joinColumns = @JoinColumn(name = "bank_id"), indexes = @Index(name = "idx_bank_currencies_bank_id", columnList = "bank_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Set<Currency> currencies;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @Column(name = "id_enterprise", nullable = false)
    private String idEnterprise;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;
}
