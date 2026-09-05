package com.example.reports;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class SecurityConfig {

    private final String clientId;
    private final String issuerUri;

    public SecurityConfig(
            @Value("${app.keycloak.client-id}") String clientId,
            @Value("${app.keycloak.issuer-uri}") String issuerUri) {
        this.clientId = clientId;
        this.issuerUri = issuerUri;
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            Converter<Jwt, Collection<GrantedAuthority>> realmRoleConverter) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/reports/admin").hasRole("REPORT_ADMIN")
                .requestMatchers("/api/reports").hasAnyRole("REPORT_VIEWER", "REPORT_ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(realmRoleConverter))));
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http, LogoutSuccessHandler oidcLogoutSuccessHandler)
            throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/error").permitAll()
            .requestMatchers("/profile").hasAnyRole("REPORT_VIEWER", "REPORT_ADMIN")
            .anyRequest().permitAll())
            .oauth2Login(oauth2Login -> oauth2Login
                .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(oidcUserAuthoritiesMapper())))
            .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler));
        return http.build();
    }

    @Bean
    LogoutSuccessHandler oidcLogoutSuccessHandler() {
        String endSessionEndpoint = issuerUri + "/protocol/openid-connect/logout";
        return (request, response, authentication) -> {
            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                String postLogoutRedirectUri = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(request.getContextPath() + "/")
                    .replaceQuery(null)
                    .build()
                    .toUriString();
                String logoutUrl = UriComponentsBuilder.fromUriString(endSessionEndpoint)
                    .queryParam("id_token_hint", URLEncoder.encode(
                        oidcUser.getIdToken().getTokenValue(), StandardCharsets.UTF_8))
                    .queryParam("post_logout_redirect_uri", URLEncoder.encode(
                        postLogoutRedirectUri, StandardCharsets.UTF_8))
                    .queryParam("client_id", URLEncoder.encode(clientId, StandardCharsets.UTF_8))
                    .build(true)
                    .toUriString();
                response.sendRedirect(logoutUrl);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/");
        };
    }

    @Bean("realmRoleConverter")
    Converter<Jwt, Collection<GrantedAuthority>> realmRoleConverter() {
        return jwt -> clientRoles(jwt.getClaimAsString("azp"), jwt.getClaimAsMap("resource_access"));
    }

    @Bean
    GrantedAuthoritiesMapper oidcUserAuthoritiesMapper() {
        return authorities -> {
            List<GrantedAuthority> mappedAuthorities = new ArrayList<>(authorities);
            authorities.stream()
                .filter(OidcUserAuthority.class::isInstance)
                .map(OidcUserAuthority.class::cast)
                .map(OidcUserAuthority::getIdToken)
                .flatMap(idToken -> clientRoles(
                    idToken.getClaimAsString("azp"),
                    idToken.getClaimAsMap("resource_access")).stream())
                .forEach(mappedAuthorities::add);
            return mappedAuthorities;
        };
    }

    private Collection<GrantedAuthority> clientRoles(String authorizedParty, Map<String, Object> resourceAccess) {
        if (!clientId.equals(authorizedParty) || resourceAccess == null
                || !(resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess)
                || !(clientAccess.get("roles") instanceof Collection<?> roles)) {
            return List.of();
        }
        return roles.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .map(GrantedAuthority.class::cast)
            .toList();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter(
            Converter<Jwt, Collection<GrantedAuthority>> realmRoleConverter) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(realmRoleConverter);
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }
}
