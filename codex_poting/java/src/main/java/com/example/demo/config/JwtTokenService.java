package com.example.demo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenService {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final String secret;
    private final long expiresInSeconds;

    public JwtTokenService(
        @Value("${app.auth.jwt-secret}") String secret,
        @Value("${app.auth.expires-in-seconds}") long expiresInSeconds
    ) {
        this.objectMapper = new ObjectMapper();
        this.clock = Clock.systemUTC();
        this.secret = secret;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String createToken(Authentication authentication) {
        final var now = clock.instant().getEpochSecond();
        final var header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
        );
        final var payload = Map.of(
            "sub", authentication.getName(),
            "iat", now,
            "exp", now + expiresInSeconds,
            "authorities", authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList()
        );
        final var headerAndPayload = encode(header) + "." + encode(payload);
        return headerAndPayload + "." + sign(headerAndPayload);
    }

    public Authentication toAuthentication(String token) {
        final var parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token format");
        }

        final var headerAndPayload = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(headerAndPayload), parts[2])) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        final var payload = decode(parts[1]);
        final var expiresAt = ((Number) payload.get("exp")).longValue();
        if (expiresAt < clock.instant().getEpochSecond()) {
            throw new IllegalArgumentException("Token expired");
        }

        final var username = (String) payload.get("sub");
        final var authorities = ((List<?>) payload.get("authorities")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .toList();
        return UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    private String encode(Map<String, ?> value) {
        try {
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to encode token", ex);
        }
    }

    private Map<String, Object> decode(String value) {
        try {
            final var json = Base64.getUrlDecoder().decode(value);
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (IllegalArgumentException | IOException ex) {
            throw new IllegalArgumentException("Failed to decode token", ex);
        }
    }

    private String sign(String value) {
        try {
            final var mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to sign token", ex);
        }
    }

    private boolean constantTimeEquals(String expected, String actual) {
        final var expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        final var actualBytes = actual.getBytes(StandardCharsets.UTF_8);
        if (expectedBytes.length != actualBytes.length) {
            return false;
        }

        var result = 0;
        for (var i = 0; i < expectedBytes.length; i++) {
            result |= expectedBytes[i] ^ actualBytes[i];
        }
        return result == 0;
    }
}
