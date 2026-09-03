package com.example.portal;

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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PortalSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("realmRoleConverter")
    private Converter<Jwt, Collection<GrantedAuthority>> realmRoleConverter;

    @Test
    void publicHomeIsAvailableAndProfileUsesOidcLogin() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/profile")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profile").with(oidcLogin())).andExpect(status().isOk());
    }

    @Test
    void logoutInitiatesOidcRpInitiatedLogout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(oidcLogin())
                .with(csrf())
                .with(request -> {
                    request.setScheme("http");
                    request.setServerName("localhost");
                    request.setServerPort(8081);
                    return request;
                }))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", startsWith(
                "http://localhost:8080/realms/sample-realm/protocol/openid-connect/logout?")))
            .andExpect(header().string("Location", containsString("id_token_hint=")))
            .andExpect(header().string("Location", containsString(
                "post_logout_redirect_uri=http%3A%2F%2Flocalhost%3A8081%2F")))
            .andExpect(header().string("Location", containsString("client_id=springboot-portal")));
    }

    @Test
    void portalUserCanAccessOwnApiButNotAdminApi() throws Exception {
        mockMvc.perform(get("/api/me").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PORTAL_USER"))))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PORTAL_USER"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void portalAdminCanAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PORTAL_ADMIN"))))
            .andExpect(status().isOk());
    }

    @Test
    void converterIgnoresReportsClientRoles() {
        Jwt jwt = jwtWithResourceAccess(Map.of(
            "springboot-portal", Map.of("roles", List.of("PORTAL_USER")),
            "springboot-reports", Map.of("roles", List.of("REPORT_ADMIN"))));
        assertThat(realmRoleConverter.convert(jwt))
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_PORTAL_USER");
    }

    @Test
    void converterRejectsTokenIssuedToReportsClient() {
        Jwt jwt = jwtWithResourceAccess("springboot-reports", Map.of(
            "springboot-portal", Map.of("roles", List.of("PORTAL_USER"))));
        assertThat(realmRoleConverter.convert(jwt)).isEmpty();
    }

    private Jwt jwtWithResourceAccess(Map<String, Object> resourceAccess) {
        return jwtWithResourceAccess("springboot-portal", resourceAccess);
    }

    private Jwt jwtWithResourceAccess(String authorizedParty, Map<String, Object> resourceAccess) {
        return Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("azp", authorizedParty)
            .claim("resource_access", resourceAccess)
            .build();
    }
}
