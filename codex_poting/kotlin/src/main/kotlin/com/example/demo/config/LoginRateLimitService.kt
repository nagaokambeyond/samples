package com.example.demo.config

import com.example.demo.exception.LoginRateLimitExceededException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Component
class LoginRateLimitService internal constructor(
    private val properties: LoginRateLimitProperties,
    private val clock: Clock
) {
    private val counters: ConcurrentHashMap<String?, LoginAttemptCounter?> =
        ConcurrentHashMap<String?, LoginAttemptCounter?>()

    @Autowired
    constructor(properties: LoginRateLimitProperties) : this(properties, Clock.system(properties.zoneId))

    fun consume(username: String?) {
        if (!properties.isEnabled()) {
            return
        }

        val today = LocalDate.now(clock.withZone(properties.zoneId))
        counters.compute(username) { key: String?, counter: LoginAttemptCounter? -> nextCounter(counter, today) }
    }

    fun resetAll() {
        counters.clear()
    }

    private fun nextCounter(counter: LoginAttemptCounter?, today: LocalDate?): LoginAttemptCounter {
        if (counter == null || counter.date != today) {
            return LoginAttemptCounter(today, 1)
        }
        if (counter.count >= properties.dailyLimit) {
            throw LoginRateLimitExceededException()
        }
        return LoginAttemptCounter(today, counter.count + 1)
    }

    private data class LoginAttemptCounter(val date: LocalDate?, val count: Int)
}
