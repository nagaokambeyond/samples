package com.example.demo.jooq.dsl

import com.example.demo.jooq.generated.Tables
import org.jooq.DSLContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class SupplierDsl(private val dsl: DSLContext) {
    fun exists(supplierId: Long?): Boolean {
        return dsl.fetchExists(Tables.SUPPLIER, Tables.SUPPLIER.ID.eq(supplierId))
    }
}
