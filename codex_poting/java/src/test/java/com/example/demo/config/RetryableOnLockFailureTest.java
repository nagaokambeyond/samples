package com.example.demo.config;

import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.doma.service.BooksOperationServiceDoma;
import com.example.demo.jpa.service.BooksOperationServiceJPA;
import com.example.demo.mybatis.service.BooksOperationServiceMybatis;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RetryableOnLockFailureTest {
    @Test
    void retryableOnLockFailureUsesLockFailureRetrySettings() {
        final var retryable = AnnotatedElementUtils.findMergedAnnotation(RetryableOnLockFailure.class, Retryable.class);

        assertThat(retryable).isNotNull();
        assertThat(retryable.includes())
            .containsExactly(CannotAcquireLockException.class, PessimisticLockingFailureException.class);
        assertThat(retryable.maxRetriesString()).isEqualTo("${app.lock-failure-retry.max-retries:3}");
        assertThat(retryable.delayString()).isEqualTo("${app.lock-failure-retry.delay:150}");
        assertThat(retryable.multiplierString()).isEqualTo("${app.lock-failure-retry.multiplier:2}");
        assertThat(retryable.maxDelayString()).isEqualTo("${app.lock-failure-retry.max-delay:1000}");
    }

    @Test
    void updateAndDeleteMethodsUseRetryableOnLockFailure() throws NoSuchMethodException {
        for (final var serviceClass : List.of(BooksOperationServiceJPA.class, BooksOperationServiceMybatis.class, BooksOperationServiceDoma.class)) {
            assertThat(serviceClass.getMethod("update", BookUpdateRequest.class).isAnnotationPresent(RetryableOnLockFailure.class))
                .isTrue();
            assertThat(serviceClass.getMethod("delete", Long.class).isAnnotationPresent(RetryableOnLockFailure.class))
                .isTrue();
        }
    }
}
