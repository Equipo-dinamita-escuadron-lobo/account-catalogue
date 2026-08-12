package com.account_catalogue.commons.multitenancy.interceptor;

import com.account_catalogue.commons.multitenancy.utils.TenantContext;
import java.util.Collection;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Component
public class TenantInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new SecurityException("No existe JWT autenticado para establecer tenant");
        }
        var jwt = jwtAuthentication.getToken();
        String requestedTenant = request.getHeader("X-Tenant-ID");
        if (requestedTenant == null || requestedTenant.isBlank()) {
            TenantContext.setTenantId(jwt.getSubject());
            return;
        }
        String azp = jwt.getClaimAsString("azp");
        if (azp != null && azp.endsWith("-service")) {
            Object claim = jwt.getClaim("tenant_ids");
            Collection<String> allowed = claim instanceof Collection<?> values
                    ? values.stream().map(String::valueOf).toList() : java.util.List.of();
            if (allowed.contains("*") || !allowed.contains(requestedTenant)) {
                throw new SecurityException("Tenant tecnico no autorizado");
            }
        } else if (!requestedTenant.equals(jwt.getSubject())) {
            throw new SecurityException("Tenant HTTP diferente del usuario autenticado");
        }
        TenantContext.setTenantId(requestedTenant);
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) {
        // The transaction may still be completing.
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) {
        TenantContext.clear();
    }
}
