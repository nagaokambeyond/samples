package com.example.demo.config

import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties(prefix = "search")
@Validated
class SearchProperties {
    @field:Min(1)
    var pageSize: Int = 0
}
