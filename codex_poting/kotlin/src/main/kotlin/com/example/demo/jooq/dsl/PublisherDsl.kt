package com.example.demo.jooq.dsl

import com.example.demo.jooq.generated.Tables
import org.jooq.DSLContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class PublisherDsl(private val dsl: DSLContext) {
    fun existsPublisher(publisherId: Long?): Boolean {
        return dsl.fetchExists(Tables.PUBLISHER, Tables.PUBLISHER.ID.eq(publisherId))
    }
}
