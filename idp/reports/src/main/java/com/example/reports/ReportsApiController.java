package com.example.reports;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ReportsApiController {

    @GetMapping("/api/reports")
    Map<String, Object> reports(Authentication authentication) {
        return Map.of("system", "reports", "message", "レポート閲覧API", "subject", authentication.getName());
    }

    @GetMapping("/api/reports/admin")
    Map<String, Object> admin(Authentication authentication) {
        return Map.of("system", "reports", "message", "レポート管理API", "subject", authentication.getName());
    }
}
