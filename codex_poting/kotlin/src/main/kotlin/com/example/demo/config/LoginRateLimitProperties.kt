package com.example.demo.config

import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import java.time.ZoneId

@Component
@ConfigurationProperties(prefix = "app.auth.login-rate-limit")
@Validated
class LoginRateLimitProperties {
    var enabled: Boolean = true

    @field:Min(1)
    var dailyLimit: Int = 10

    var zoneId: ZoneId = ZoneId.of("Asia/Tokyo")

    fun isEnabled(): Boolean = enabled
}
