package com.example.demo.config;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
    includes = {
        CannotAcquireLockException.class,
        PessimisticLockingFailureException.class
    },
    maxRetries = 3,
    delay = 150,
    multiplier = 2,
    maxDelay = 1000
)
public @interface RetryableOnLockFailure {
}
