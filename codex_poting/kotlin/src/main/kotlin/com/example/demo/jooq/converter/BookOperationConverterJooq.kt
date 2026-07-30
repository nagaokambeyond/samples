package com.example.demo.jooq.converter

import com.example.demo.api.response.BookResponse
import com.example.demo.api.response.BookStockResponse
import com.example.demo.jooq.entity.BookWithStockRow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class BookOperationConverterJooq {
    fun toResponse(rows: MutableList<BookWithStockRow?>): BookResponse? {
        return toResponseList(rows).first()
    }

    fun toResponseList(rows: MutableList<BookWithStockRow?>): MutableList<BookResponse> {
        val responses = LinkedHashMap<Long?, BookResponse>()
        rows.filterNotNull().forEach { row ->
            val response = responses.computeIfAbsent(row.id) { toBookResponse(row) }
            if (row.bookStockId != null) {
                (response.bookStockList as MutableList<BookStockResponse>).add(toBookStockResponse(row))
            }
        }
        return ArrayList(responses.values)
    }

    private fun toBookResponse(row: BookWithStockRow): BookResponse {
        val response = BookResponse()
        response.id = row.id
        response.title = row.title
        response.author = row.author
        response.releaseDate = row.releaseDate
        response.publisherId = row.publisherId
        response.publisherName = row.publisherName
        response.genreId = row.genreId
        response.genreName = row.genreName
        response.isbn = row.isbn
        response.salesUnitPrice = row.salesUnitPrice
        response.updateAt = row.updateAt
        response.version = row.version
        response.bookStockList = ArrayList()
        return response
    }

    private fun toBookStockResponse(row: BookWithStockRow): BookStockResponse {
        val response = BookStockResponse()
        response.id = row.bookStockId
        response.bookStockStoreId = row.bookStockStoreId
        response.storeName = row.storeName
        response.bookStockQuantity = row.bookStockQuantity
        return response
    }
}
