package com.example.demo.config

import com.example.demo.exception.LoginRateLimitExceededException
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class LoginRateLimitServiceTest {
    @Test
    fun consumeThrowsWhenDailyLimitIsExceeded() {
        val service = createService(2, Instant.parse("2026-07-01T00:00:00Z"))

        service.consume("admin")
        service.consume("admin")

        Assertions.assertThatThrownBy(ThrowingCallable { service.consume("admin") })
            .isInstanceOf(LoginRateLimitExceededException::class.java)
    }

    @Test
    fun consumeCountsByUsername() {
        val service = createService(1, Instant.parse("2026-07-01T00:00:00Z"))

        service.consume("admin")

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { service.consume("operator") })
    }

    @Test
    fun consumeResetsCounterWhenDateChanges() {
        val clock = MutableClock(Instant.parse("2026-07-01T00:00:00Z"), ZONE_ID)
        val service = createService(1, clock)
        service.consume("admin")

        clock.setInstant(Instant.parse("2026-07-02T00:00:00Z"))

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { service.consume("admin") })
    }

    @Test
    fun consumeDoesNothingWhenDisabled() {
        val properties = createProperties(1)
        properties.enabled = false
        val service = LoginRateLimitService(
            properties,
            Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZONE_ID)
        )

        service.consume("admin")

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { service.consume("admin") })
    }

    @Test
    fun resetAllClearsCounters() {
        val service = createService(1, Instant.parse("2026-07-01T00:00:00Z"))
        service.consume("admin")

        service.resetAll()

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { service.consume("admin") })
    }

    private fun createService(dailyLimit: Int, instant: Instant): LoginRateLimitService {
        return createService(dailyLimit, Clock.fixed(instant, ZONE_ID))
    }

    private fun createService(dailyLimit: Int, clock: Clock): LoginRateLimitService {
        return LoginRateLimitService(createProperties(dailyLimit), clock)
    }

    private fun createProperties(dailyLimit: Int): LoginRateLimitProperties {
        val properties = LoginRateLimitProperties()
        properties.dailyLimit = dailyLimit
        properties.zoneId = ZONE_ID
        return properties
    }

    private class MutableClock(private var instant: Instant?, private val zone: ZoneId?) : Clock() {
        override fun getZone(): ZoneId? {
            return zone
        }

        override fun withZone(zone: ZoneId?): Clock {
            return MutableClock(instant, zone)
        }

        override fun instant(): Instant? {
            return instant
        }

        fun setInstant(instant: Instant?) {
            this.instant = instant
        }
    }

    companion object {
        private val ZONE_ID: ZoneId = ZoneId.of("Asia/Tokyo")
    }
}
