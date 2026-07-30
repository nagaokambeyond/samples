package com.example.demo.jooq.entity

import java.time.LocalDate

data class BookSalesUnitPriceHistoryRow(
    val id: Long?,
    val effectiveFrom: LocalDate?,
    val version: Long?
)
