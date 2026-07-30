package com.example.demo.openbd.config

import com.example.demo.openbd.generated.api.BooksApi
import com.example.demo.openbd.generated.api.MetadataApi
import com.example.demo.openbd.generated.invoker.ApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenBdClientConfig {
    @Bean
    fun openBdApiClient(properties: OpenBdProperties): ApiClient {
        val apiClient = ApiClient()
        apiClient.updateBaseUri(properties.baseUrl)
        return apiClient
    }

    @Bean
    fun openBdBooksApi(openBdApiClient: ApiClient): BooksApi {
        return BooksApi(openBdApiClient)
    }

    @Bean
    fun openBdMetadataApi(openBdApiClient: ApiClient): MetadataApi {
        return MetadataApi(openBdApiClient)
    }
}
