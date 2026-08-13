package com.example.demo.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "search")
@Validated
@Getter
@Setter
public class SearchProperties {
    @Min(1)
    private int pageSize;
}
