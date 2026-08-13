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
    maxRetriesString = "${app.lock-failure-retry.max-retries:3}",
    delayString = "${app.lock-failure-retry.delay:150}",
    multiplierString = "${app.lock-failure-retry.multiplier:2}",
    maxDelayString = "${app.lock-failure-retry.max-delay:1000}"
)
public @interface RetryableOnLockFailure {
}
