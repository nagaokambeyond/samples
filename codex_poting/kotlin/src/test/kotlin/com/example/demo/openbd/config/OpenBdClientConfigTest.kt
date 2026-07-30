package com.example.demo.openbd.config

import com.example.demo.openbd.generated.api.BooksApi
import com.example.demo.openbd.generated.api.MetadataApi
import com.example.demo.openbd.generated.invoker.ApiClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.ContextConsumer

internal class OpenBdClientConfigTest {
    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration::class.java))
        .withUserConfiguration(OpenBdProperties::class.java, OpenBdClientConfig::class.java)
        .withPropertyValues("openbd.base-url=https://example.test/openbd")

    @Test
    fun createsGeneratedOpenBdApiBeans() {
        contextRunner.run(ContextConsumer { context: AssertableApplicationContext? ->
            Assertions.assertThat(context!!.getBeansOfType(ApiClient::class.java)).hasSize(1)
            Assertions.assertThat(context.getBeansOfType(BooksApi::class.java)).hasSize(1)
            Assertions.assertThat(context.getBeansOfType(MetadataApi::class.java)).hasSize(1)
            Assertions.assertThat(context.getBean(ApiClient::class.java).baseUri)
                .isEqualTo("https://example.test/openbd")
        })
    }
}
