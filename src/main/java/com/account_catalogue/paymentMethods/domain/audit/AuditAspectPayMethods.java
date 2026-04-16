package com.account_catalogue.paymentMethods.domain.audit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.account_catalogue.commons.audit.annotation.Auditable;
import com.account_catalogue.commons.audit.aspect.BaseAuditAspect;
import com.account_catalogue.commons.audit.builder.AuditEventBuilder;
import com.account_catalogue.commons.audit.publisher.AuditEventPublisher;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.domain.services.IPaymentMethodService;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuditAspectPayMethods extends BaseAuditAspect {

    @Lazy
    private final IPaymentMethodService paymentMethodService;

    public AuditAspectPayMethods(AuditEventBuilder auditEventBuilder,
            AuditEventPublisher auditEventPublisher, @Lazy IPaymentMethodService paymentMethodService) {
        super(auditEventBuilder, auditEventPublisher);
        this.paymentMethodService = paymentMethodService;
    }

    @Around("@annotation(auditable) && within(com.account_catalogue.paymentMethods.domain.services..*)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        return executeAudit(joinPoint, auditable);
    }

    @Override
    protected Map<String, Object> fetchCurrentState(Auditable auditable, Object[] args) {
        return switch (auditable.affectedTable()) {
            case "PAYMENT_METHOD" -> {
                Long id = (args[0] instanceof Long) ? (Long) args[0] : ((PaymentMethodUpdateReq) args[0]).getId();
                String enterpriseId = (args[0] instanceof Long) ? (String) args[1]
                        : ((PaymentMethodUpdateReq) args[0]).getIdEnterprise();
                yield Optional.ofNullable(paymentMethodService.findById(id, enterpriseId))
                        .map(this::entityToMap)
                        .orElse(null);
            }
            default -> null;
        };
    }

    @Override
    protected String resolveEnterpriseId(Auditable auditable, Object[] args, Object result,
            Map<String, Object> beforeData) {
        return switch (auditable.operationType()) {
            case CREATE -> result instanceof PaymentMethod p ? p.getIdEnterprise() : "UNKNOWN";
            case DELETE, INACTIVATE, ACTIVATE -> (String) args[auditable.enterpriseIdArgIndex()];
            default -> beforeData != null ? (String) beforeData.get("entId") : "UNKNOWN";
        };
    }

    @Override
    protected String resolveRegisterId(Auditable auditable, Object[] args, Object result,
            Map<String, Object> beforeData) {
        return switch (auditable.operationType()) {
            case CREATE -> result instanceof PaymentMethod p ? String.valueOf(p.getId()) : "UNKNOWN";
            case DELETE, INACTIVATE, ACTIVATE -> String.valueOf(args[auditable.idArgIndex()]);
            default -> beforeData != null ? String.valueOf(beforeData.get("id")) : "UNKNOWN";
        };
    }

    @Override
    protected Map<String, Object> buildContext(Object[] args, Object result, Map<String, Object> beforeData) {
        if (beforeData == null)
            return Map.of();
        Map<String, Object> context = new LinkedHashMap<>();
        if (beforeData.get("name") != null)
            context.put("name", beforeData.get("name"));
        return context;
    }

    @Override
    protected Map<String, Object> entityToMap(Object object) {
        if (!(object instanceof PaymentMethod paymentMethod)) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", paymentMethod.getId());
        map.put("entId", paymentMethod.getIdEnterprise());
        map.put("name", paymentMethod.getName());
        map.put("AccountCatalogueId", paymentMethod.getAccountingAccountEntity().getId());
        map.put("accountingAccount", paymentMethod.getAccountingAccount());
        map.put("state", paymentMethod.getStatus());

        map.entrySet().removeIf(entry -> entry.getValue() == null);
        return map;
    }

}
