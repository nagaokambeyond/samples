package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePessimisticLockingFailureExceptionReturnsConflict() {
        final var problem = handler.handlePessimisticLockingFailureException(new CannotAcquireLockException("lock failed"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("更新競合");
        assertThat(problem.getDetail()).isEqualTo("他ユーザーによって更新されています");
    }
}
