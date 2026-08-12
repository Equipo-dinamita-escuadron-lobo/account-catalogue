package com.account_catalogue.unit.commons.multitenancy.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.account_catalogue.commons.multitenancy.interceptor.TenantInterceptor;
import com.account_catalogue.commons.multitenancy.utils.TenantContext;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.WebRequest;

@DisplayName("TenantInterceptor - contrato PP8 de aislamiento tenant")
class TenantInterceptorUnitTest {

    private final TenantInterceptor interceptor = new TenantInterceptor();
    private WebRequest request;

    @BeforeEach
    void setUp() {
        request = mock(WebRequest.class);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void humanTokenWithoutHeaderUsesSubject() {
        authenticate("tenant-123", "microservices_client", null);

        interceptor.preHandle(request);

        assertEquals("tenant-123", TenantContext.getTenantId());
    }

    @Test
    void humanTokenAcceptsOnlyHeaderEqualToSubject() {
        authenticate("tenant-123", "microservices_client", null);
        when(request.getHeader("X-Tenant-ID")).thenReturn("tenant-123");
        interceptor.preHandle(request);
        assertEquals("tenant-123", TenantContext.getTenantId());

        TenantContext.clear();
        when(request.getHeader("X-Tenant-ID")).thenReturn("tenant-other");
        assertThrows(SecurityException.class, () -> interceptor.preHandle(request));
    }

    @Test
    void technicalTokenAcceptsTenantIncludedInTenantIds() {
        authenticate("service-account", "account-catalogue-service", List.of("tenant-a", "tenant-b"));
        when(request.getHeader("X-Tenant-ID")).thenReturn("tenant-b");

        interceptor.preHandle(request);

        assertEquals("tenant-b", TenantContext.getTenantId());
    }

    @Test
    void technicalTokenRejectsTenantNotIncludedInTenantIds() {
        authenticate("service-account", "account-catalogue-service", List.of("tenant-a"));
        when(request.getHeader("X-Tenant-ID")).thenReturn("tenant-b");

        assertThrows(SecurityException.class, () -> interceptor.preHandle(request));
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void technicalTokenRejectsWildcardEvenWhenRequestedTenantIsPresent() {
        authenticate("service-account", "account-catalogue-service", List.of("*", "tenant-a"));
        when(request.getHeader("X-Tenant-ID")).thenReturn("tenant-a");

        assertThrows(SecurityException.class, () -> interceptor.preHandle(request));
    }

    @Test
    void anonymousRequestIsRejectedInsteadOfUsingDefaultTenant() {
        assertThrows(SecurityException.class, () -> interceptor.preHandle(request));
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void postHandleKeepsTenantUntilTransactionCompletion() {
        TenantContext.setTenantId("tenant-live");

        interceptor.postHandle(request, null);

        assertEquals("tenant-live", TenantContext.getTenantId());
    }

    @Test
    void afterCompletionAlwaysClearsTenantWithOrWithoutError() {
        TenantContext.setTenantId("tenant-success");
        interceptor.afterCompletion(request, null);
        assertNull(TenantContext.getTenantId());

        TenantContext.setTenantId("tenant-error");
        assertDoesNotThrow(() -> interceptor.afterCompletion(request, new RuntimeException("request failed")));
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void fullRequestCycleRetainsThenClearsTenant() {
        authenticate("tenant-cycle", "microservices_client", null);

        interceptor.preHandle(request);
        interceptor.postHandle(request, null);
        assertEquals("tenant-cycle", TenantContext.getTenantId());

        interceptor.afterCompletion(request, null);
        assertNull(TenantContext.getTenantId());
    }

    private void authenticate(String subject, String azp, List<String> tenantIds) {
        Jwt.Builder builder = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("azp", azp)
                .claims(claims -> {
                    if (tenantIds != null) {
                        claims.put("tenant_ids", tenantIds);
                    }
                    claims.put("resource_access", Map.of());
                });
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(builder.build()));
    }
}
