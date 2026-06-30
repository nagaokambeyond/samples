package com.example.demo.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;

@Component
@ConfigurationProperties(prefix = "app.auth.login-rate-limit")
@Validated
@Getter
@Setter
public class LoginRateLimitProperties {
    private boolean enabled = true;

    @Min(1)
    private int dailyLimit = 10;

    private ZoneId zoneId = ZoneId.of("Asia/Tokyo");
}
