package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.multitenancy.async;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.multitenancy.utils.TenantContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        String tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                TenantContext.setTenantId(null);
            }
        };
    }
}