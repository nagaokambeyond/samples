package com.example.demo.config;

import com.example.demo.exception.LoginRateLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitService {
    private final LoginRateLimitProperties properties;
    private final Clock clock;
    private final ConcurrentHashMap<String, LoginAttemptCounter> counters = new ConcurrentHashMap<>();

    @Autowired
    public LoginRateLimitService(LoginRateLimitProperties properties) {
        this(properties, Clock.system(properties.getZoneId()));
    }

    LoginRateLimitService(LoginRateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public void consume(String username) {
        if (!properties.isEnabled()) {
            return;
        }

        final var today = LocalDate.now(clock.withZone(properties.getZoneId()));
        counters.compute(username, (key, counter) -> nextCounter(counter, today));
    }

    public void resetAll() {
        counters.clear();
    }

    private LoginAttemptCounter nextCounter(LoginAttemptCounter counter, LocalDate today) {
        if (counter == null || !counter.date().equals(today)) {
            return new LoginAttemptCounter(today, 1);
        }
        if (counter.count() >= properties.getDailyLimit()) {
            throw new LoginRateLimitExceededException();
        }
        return new LoginAttemptCounter(today, counter.count() + 1);
    }

    private record LoginAttemptCounter(LocalDate date, int count) {
    }
}
