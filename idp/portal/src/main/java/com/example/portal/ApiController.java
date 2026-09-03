package com.example.portal;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ApiController {

    @GetMapping("/api/me")
    Map<String, Object> me(Authentication authentication) {
        return Map.of("system", "portal", "message", "ポータル利用者向けAPI", "subject", authentication.getName());
    }

    @GetMapping("/api/admin")
    Map<String, Object> admin(Authentication authentication) {
        return Map.of("system", "portal", "message", "ポータル管理者向けAPI", "subject", authentication.getName());
    }
}
