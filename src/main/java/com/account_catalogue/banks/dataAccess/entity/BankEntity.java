package com.account_catalogue.banks.dataAccess.entity;

import com.account_catalogue.banks.domain.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

@Entity
@Table(
    name = "banks",
    indexes = {
        @Index(name = "idx_bank_id_enterprise", columnList = "id_enterprise"),
        @Index(name = "idx_bank_codigo", columnList = "codigo"),
        @Index(name = "idx_bank_nombre", columnList = "nombre"),
        @Index(name = "idx_bank_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_bank_enterprise_deleted", columnList = "id_enterprise, is_deleted")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false)
    private Currency moneda;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "id_enterprise", nullable = false)
    private String idEnterprise;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;
}
