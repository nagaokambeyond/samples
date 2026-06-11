package com.example.demo.config;

import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.doma.service.BookServiceDoma;
import com.example.demo.jpa.service.BookServiceJPA;
import com.example.demo.mybatis.service.BookServiceMybatis;
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
        assertThat(retryable.maxRetries()).isEqualTo(3);
        assertThat(retryable.delay()).isEqualTo(500);
        assertThat(retryable.multiplier()).isEqualTo(2);
        assertThat(retryable.maxDelay()).isEqualTo(3000);
    }

    @Test
    void updateAndDeleteMethodsUseRetryableOnLockFailure() throws NoSuchMethodException {
        for (final var serviceClass : List.of(BookServiceJPA.class, BookServiceMybatis.class, BookServiceDoma.class)) {
            assertThat(serviceClass.getMethod("update", BookUpdateRequest.class).isAnnotationPresent(RetryableOnLockFailure.class))
                .isTrue();
            assertThat(serviceClass.getMethod("delete", Long.class).isAnnotationPresent(RetryableOnLockFailure.class))
                .isTrue();
        }
    }
}
