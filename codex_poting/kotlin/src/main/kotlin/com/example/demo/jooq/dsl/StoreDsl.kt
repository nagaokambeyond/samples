package com.example.demo.jooq.dsl

import com.example.demo.jooq.generated.Tables
import org.jooq.DSLContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class StoreDsl(private val dsl: DSLContext) {
    fun exists(storeId: Long?): Boolean {
        return dsl.fetchExists(Tables.STORE, Tables.STORE.ID.eq(storeId))
    }
}
