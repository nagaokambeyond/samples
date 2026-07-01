package com.example.demo.config;

import com.example.demo.exception.LoginRateLimitExceededException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginRateLimitServiceTest {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Tokyo");

    @Test
    void consumeThrowsWhenDailyLimitIsExceeded() {
        final var service = createService(2, Instant.parse("2026-07-01T00:00:00Z"));

        service.consume("admin");
        service.consume("admin");

        assertThatThrownBy(() -> service.consume("admin"))
            .isInstanceOf(LoginRateLimitExceededException.class);
    }

    @Test
    void consumeCountsByUsername() {
        final var service = createService(1, Instant.parse("2026-07-01T00:00:00Z"));

        service.consume("admin");

        assertThatNoException().isThrownBy(() -> service.consume("operator"));
    }

    @Test
    void consumeResetsCounterWhenDateChanges() {
        final var clock = new MutableClock(Instant.parse("2026-07-01T00:00:00Z"), ZONE_ID);
        final var service = createService(1, clock);
        service.consume("admin");

        clock.setInstant(Instant.parse("2026-07-02T00:00:00Z"));

        assertThatNoException().isThrownBy(() -> service.consume("admin"));
    }

    @Test
    void consumeDoesNothingWhenDisabled() {
        final var properties = createProperties(1);
        properties.setEnabled(false);
        final var service = new LoginRateLimitService(
            properties,
            Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZONE_ID)
        );

        service.consume("admin");

        assertThatNoException().isThrownBy(() -> service.consume("admin"));
    }

    @Test
    void resetAllClearsCounters() {
        final var service = createService(1, Instant.parse("2026-07-01T00:00:00Z"));
        service.consume("admin");

        service.resetAll();

        assertThatNoException().isThrownBy(() -> service.consume("admin"));
    }

    private LoginRateLimitService createService(int dailyLimit, Instant instant) {
        return createService(dailyLimit, Clock.fixed(instant, ZONE_ID));
    }

    private LoginRateLimitService createService(int dailyLimit, Clock clock) {
        return new LoginRateLimitService(createProperties(dailyLimit), clock);
    }

    private LoginRateLimitProperties createProperties(int dailyLimit) {
        final var properties = new LoginRateLimitProperties();
        properties.setDailyLimit(dailyLimit);
        properties.setZoneId(ZONE_ID);
        return properties;
    }

    private static class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zone;

        private MutableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void setInstant(Instant instant) {
            this.instant = instant;
        }
    }
}
