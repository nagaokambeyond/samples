package com.example.demo.jooq.entity

import java.time.LocalDate
import java.time.LocalDateTime

data class BookWithStockRow(
    val id: Long?,
    val title: String?,
    val author: String?,
    val releaseDate: LocalDate?,
    val publisherId: Long?,
    val publisherName: String?,
    val genreId: Long?,
    val genreName: String?,
    val isbn: String?,
    val salesUnitPrice: Int?,
    val updateAt: LocalDateTime?,
    val version: Long?,
    val bookStockId: Long?,
    val bookStockStoreId: Long?,
    val storeName: String?,
    val bookStockQuantity: Int?
)
