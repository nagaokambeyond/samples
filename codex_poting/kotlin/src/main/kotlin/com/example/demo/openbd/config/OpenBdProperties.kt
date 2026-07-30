package com.example.demo.openbd.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties(prefix = "openbd")
@Validated
class OpenBdProperties {
    var baseUrl: String = "https://api.openbd.jp"
}
