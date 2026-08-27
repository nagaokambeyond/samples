package com.example.javaobjectmapper;

import com.example.javaobjectmapper.config.BenchmarkProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BenchmarkProperties.class)
public class JavaObjectMapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaObjectMapperApplication.class, args);
    }
}
