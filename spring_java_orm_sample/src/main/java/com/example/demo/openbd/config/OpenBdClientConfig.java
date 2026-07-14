package com.example.demo.openbd.config;

import com.example.demo.openbd.generated.api.BooksApi;
import com.example.demo.openbd.generated.api.MetadataApi;
import com.example.demo.openbd.generated.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenBdClientConfig {

    @Bean
    ApiClient openBdApiClient(OpenBdProperties properties) {
        final var apiClient = new ApiClient();
        apiClient.updateBaseUri(properties.getBaseUrl());
        return apiClient;
    }

    @Bean
    BooksApi openBdBooksApi(ApiClient openBdApiClient) {
        return new BooksApi(openBdApiClient);
    }

    @Bean
    MetadataApi openBdMetadataApi(ApiClient openBdApiClient) {
        return new MetadataApi(openBdApiClient);
    }
}
