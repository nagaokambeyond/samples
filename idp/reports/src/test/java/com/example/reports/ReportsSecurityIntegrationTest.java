package com.example.reports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ReportsSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("realmRoleConverter")
    private Converter<Jwt, Collection<GrantedAuthority>> realmRoleConverter;

    @Autowired
    private GrantedAuthoritiesMapper oidcUserAuthoritiesMapper;

    @Test
    void publicHomeIsAvailableAndProfileRequiresReportsRole() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/profile")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profile").with(oidcLogin()
                .authorities(new SimpleGrantedAuthority("ROLE_REPORT_VIEWER"))))
            .andExpect(status().isOk());
        mockMvc.perform(get("/profile").with(oidcLogin()
                .authorities(new SimpleGrantedAuthority("ROLE_PORTAL_USER"))))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/profile").with(oidcLogin())).andExpect(status().isForbidden());
    }

    @Test
    void logoutInitiatesOidcRpInitiatedLogout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(oidcLogin())
                .with(csrf())
                .with(request -> {
                    request.setScheme("http");
                    request.setServerName("localhost");
                    request.setServerPort(8082);
                    return request;
                }))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", startsWith(
                "http://localhost:8080/realms/sample-realm/protocol/openid-connect/logout?")))
            .andExpect(header().string("Location", containsString("id_token_hint=")))
            .andExpect(header().string("Location", containsString(
                "post_logout_redirect_uri=http%3A%2F%2Flocalhost%3A8082%2F")))
            .andExpect(header().string("Location", containsString("client_id=springboot-reports")));
    }

    @Test
    void reportViewerCanAccessReportsButNotAdminApi() throws Exception {
        mockMvc.perform(get("/api/reports").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_VIEWER"))))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/reports/admin").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_VIEWER"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void reportAdminCanAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/reports/admin").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_ADMIN"))))
            .andExpect(status().isOk());
    }

    @Test
    void converterIgnoresPortalClientRoles() {
        Jwt jwt = jwtWithResourceAccess(Map.of(
            "springboot-portal", Map.of("roles", List.of("PORTAL_ADMIN")),
            "springboot-reports", Map.of("roles", List.of("REPORT_VIEWER"))));
        assertThat(realmRoleConverter.convert(jwt))
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_REPORT_VIEWER");
    }

    @Test
    void converterRejectsTokenIssuedToPortalClient() {
        Jwt jwt = jwtWithResourceAccess("springboot-portal", Map.of(
            "springboot-reports", Map.of("roles", List.of("REPORT_VIEWER"))));
        assertThat(realmRoleConverter.convert(jwt)).isEmpty();
    }

    @Test
    void oidcMapperAddsOnlyReportsRolesFromReportsIdToken() {
        OidcIdToken idToken = oidcIdToken("springboot-reports", Map.of(
            "springboot-portal", Map.of("roles", List.of("PORTAL_USER")),
            "springboot-reports", Map.of("roles", List.of("REPORT_VIEWER"))));

        assertThat(oidcUserAuthoritiesMapper.mapAuthorities(List.of(new OidcUserAuthority(idToken))))
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_REPORT_VIEWER")
            .doesNotContain("ROLE_PORTAL_USER");
    }

    @Test
    void oidcMapperRejectsIdTokenIssuedToPortalClient() {
        OidcIdToken idToken = oidcIdToken("springboot-portal", Map.of(
            "springboot-reports", Map.of("roles", List.of("REPORT_VIEWER"))));

        assertThat(oidcUserAuthoritiesMapper.mapAuthorities(List.of(new OidcUserAuthority(idToken))))
            .extracting(GrantedAuthority::getAuthority)
            .doesNotContain("ROLE_REPORT_VIEWER");
    }

    private Jwt jwtWithResourceAccess(Map<String, Object> resourceAccess) {
        return jwtWithResourceAccess("springboot-reports", resourceAccess);
    }

    private Jwt jwtWithResourceAccess(String authorizedParty, Map<String, Object> resourceAccess) {
        return Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("azp", authorizedParty)
            .claim("resource_access", resourceAccess)
            .build();
    }

    private OidcIdToken oidcIdToken(String authorizedParty, Map<String, Object> resourceAccess) {
        return new OidcIdToken("test-token", null, null, Map.of(
            "azp", authorizedParty,
            "resource_access", resourceAccess));
    }
}
