package com.account_catalogue.bankAccounts.dataAccess.entity;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

@Entity
@Table(
    name = "bank_accounts",
    indexes = {
        @Index(name = "idx_bank_account_id_enterprise", columnList = "id_enterprise"),
        @Index(name = "idx_bank_account_account_number", columnList = "account_number"),
        @Index(name = "idx_bank_account_bank_id", columnList = "bank_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private BankEntity bank;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "cuenta_contable", nullable = false)
    private String cuentaContable;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @Column(name = "id_enterprise", nullable = false)
    private String idEnterprise;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;
}
