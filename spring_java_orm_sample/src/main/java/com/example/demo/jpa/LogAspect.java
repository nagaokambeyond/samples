package com.example.demo.jpa;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("jpa")
@Slf4j
public class LogAspect {
    @Around("execution(* com.example.demo.jpa.repository.*..*(..))")
    public Object monitor(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();  // メソッド開始前のシステム時刻
        final Object proceed = joinPoint.proceed();     // メソッド実行
        final long end = System.currentTimeMillis();    // メソッド終了後のシステム時刻
        log.info("{} {}   method latency: {} ms.",      // メソッドの実行時間の出力
            joinPoint.getTarget().getClass().getName(),
            joinPoint.getSignature().getName(),
            end - start
        );
        return proceed;
    }
}
