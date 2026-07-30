package com.example.demo.service

import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookPageResponse
import com.example.demo.api.response.BookResponse
import java.time.LocalDate

interface BooksOperationService {
    fun findById(id: Long): BookResponse?

    fun search(
        keyword: String?,
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?,
        page: Int,
        size: Int
    ): BookPageResponse?

    fun create(request: BookCreateRequest): BookResponse?

    fun update(request: BookUpdateRequest): BookResponse?

    fun createSalesUnitPrice(bookId: Long, request: BookSalesUnitPriceCreateRequest)

    fun delete(id: Long)
}
