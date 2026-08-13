package com.example.demo.openbd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "openbd")
@Validated
@Getter
@Setter
public class OpenBdProperties {
    private String baseUrl = "https://api.openbd.jp";
}
