package com.example.demo.config

import org.springframework.dao.CannotAcquireLockException
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.resilience.annotation.Retryable

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@Retryable(
    includes = [CannotAcquireLockException::class, PessimisticLockingFailureException::class
    ],
    maxRetriesString = "\${app.lock-failure-retry.max-retries:3}",
    delayString = "\${app.lock-failure-retry.delay:150}",
    multiplierString = "\${app.lock-failure-retry.multiplier:2}",
    maxDelayString = "\${app.lock-failure-retry.max-delay:1000}"
)
annotation class RetryableOnLockFailure 
