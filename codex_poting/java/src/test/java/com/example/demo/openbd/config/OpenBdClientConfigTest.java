package com.example.demo.openbd.config;

import com.example.demo.openbd.generated.api.BooksApi;
import com.example.demo.openbd.generated.api.MetadataApi;
import com.example.demo.openbd.generated.invoker.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class OpenBdClientConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
        .withUserConfiguration(OpenBdProperties.class, OpenBdClientConfig.class)
        .withPropertyValues("openbd.base-url=https://example.test/openbd");

    @Test
    void createsGeneratedOpenBdApiBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ApiClient.class);
            assertThat(context).hasSingleBean(BooksApi.class);
            assertThat(context).hasSingleBean(MetadataApi.class);
            assertThat(context.getBean(ApiClient.class).getBaseUri()).isEqualTo("https://example.test/openbd");
        });
    }
}
