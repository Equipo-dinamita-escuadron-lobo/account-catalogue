package com.account_catalogue.paymentMethods.dataAccess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

@Entity
@Table(
    name = "payment_methods",
    indexes = {
        @Index(name = "idx_payment_method_id_enterprise", columnList = "id_enterprise"),
        @Index(name = "idx_payment_method_name", columnList = "name"),
        @Index(name = "idx_payment_method_status", columnList = "status"),
        @Index(name = "idx_payment_method_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_payment_method_enterprise_deleted", columnList = "id_enterprise, is_deleted")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "accounting_account", nullable = false)
    private String accountingAccount;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "id_enterprise", nullable = false)
    private String idEnterprise;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;
}
