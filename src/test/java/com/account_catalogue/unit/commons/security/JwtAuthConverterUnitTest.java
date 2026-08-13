package com.account_catalogue.unit.commons.security;

import com.account_catalogue.commons.security.JwtAuthConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JwtAuthConverter - current Keycloak authorization contract")
class JwtAuthConverterUnitTest {

    private JwtAuthConverter converter;
    private Map<String, Object> claims;

    @BeforeEach
    void setUp() {
        converter = new JwtAuthConverter();
        ReflectionTestUtils.setField(converter, "principleAtrribute", null);
        claims = new HashMap<>();
        claims.put("sub", "user-123");
        claims.put("preferred_username", "john.doe");
    }

    @Test
    void convertsTokenWithoutPermissions() {
        AbstractAuthenticationToken result = converter.convert(jwt());
        assertInstanceOf(JwtAuthenticationToken.class, result);
        assertEquals("user-123", result.getName());
        assertNotNull(result.getAuthorities());
    }

    @Test
    void extractsResourceAndScopedPermissions() {
        permission("treasury", List.of("read", "post"));
        assertEquals(Set.of("treasury", "treasury#read", "treasury#post"), authorities());
    }

    @Test
    void tokenWithoutAuthorizationHasNoCustomAuthorities() {
        assertTrue(authorities().isEmpty());
    }

    @Test
    void authorizationWithoutPermissionsHasNoCustomAuthorities() {
        claims.put("authorization", Map.of());
        assertTrue(authorities().isEmpty());
    }

    @Test
    void resourceWithoutScopesStillProducesResourceAuthority() {
        permission("accounting", null);
        assertEquals(Set.of("accounting"), authorities());
    }

    @Test
    void customPrincipalAttributeIsUsed() {
        ReflectionTestUtils.setField(converter, "principleAtrribute", "preferred_username");
        assertEquals("john.doe", converter.convert(jwt()).getName());
    }

    @Test
    void nullPrincipalAttributeUsesSubject() {
        assertEquals("user-123", converter.convert(jwt()).getName());
    }

    @Test
    void extractsMultiplePermissionResources() {
        claims.put("authorization", Map.of("permissions", List.of(
                Map.of("rsname", "treasury", "scopes", List.of("read")),
                Map.of("rsname", "catalogue", "scopes", List.of("write")))));
        assertEquals(Set.of("treasury", "treasury#read", "catalogue", "catalogue#write"), authorities());
    }

    @Test
    void emptyPermissionListHasNoCustomAuthorities() {
        claims.put("authorization", Map.of("permissions", List.of()));
        assertTrue(authorities().isEmpty());
    }

    @Test
    void getIdReturnsSubjectAfterConversion() {
        converter.convert(jwt());
        assertEquals("user-123", converter.getId());
    }

    @Test
    void getIdReflectsDifferentSubject() {
        claims.put("sub", "tenant-456");
        converter.convert(jwt());
        assertEquals("tenant-456", converter.getId());
    }

    @Test
    void getTokenReturnsRawToken() {
        converter.convert(jwt());
        assertEquals("mock-token-value", converter.getToken());
    }

    @Test
    void nullAuthorizationIsHandled() {
        claims.put("authorization", null);
        assertDoesNotThrow(this::authorities);
        assertTrue(authorities().isEmpty());
    }

    @Test
    void trimsNamesAndIgnoresMalformedOrBlankPermissions() {
        claims.put("authorization", Map.of("permissions", List.of(
                Map.of("rsname", " treasury ", "scopes", List.of(" read ", " ", 7)),
                Map.of("rsname", " "),
                Map.of("unexpected", "value"))));
        assertEquals(Set.of("treasury", "treasury#read"), authorities());
    }

    @Test
    void realmRolesExcludeKeycloakSystemRoles() {
        claims.put("realm_access", Map.of("roles", List.of(
                "Estudiante", "Administrador", "offline_access", "uma_authorization",
                "default-roles-oauth2-realm")));
        converter.convert(jwt());
        assertEquals(List.of("Estudiante", "Administrador"), converter.getRealmRoles());
    }

    private void permission(String resource, List<String> scopes) {
        Map<String, Object> permission = new HashMap<>();
        permission.put("rsname", resource);
        if (scopes != null) {
            permission.put("scopes", scopes);
        }
        claims.put("authorization", Map.of("permissions", List.of(permission)));
    }

    private Set<String> authorities() {
        return converter.convert(jwt()).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private Jwt jwt() {
        return new Jwt("mock-token-value", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256", "typ", "JWT"), claims);
    }
}
