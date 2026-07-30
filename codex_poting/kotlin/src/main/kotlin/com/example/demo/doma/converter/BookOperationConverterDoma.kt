package com.example.demo.doma.converter

import com.example.demo.api.response.BookResponse
import com.example.demo.api.response.BookStockResponse
import com.example.demo.doma.entity.BookStockWithStoreName
import com.example.demo.doma.entity.BookWithPublisherName
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("doma")
class BookOperationConverterDoma(private val modelMapper: ModelMapper) {
    fun toResponse(book: BookWithPublisherName): BookResponse {
        val response = modelMapper.map<BookResponse>(book, BookResponse::class.java)
        response.bookStockList = book.bookStockList!!.filterNotNull()
            .map { bookStock: BookStockWithStoreName ->
                modelMapper.map<BookStockResponse>(
                    bookStock,
                    BookStockResponse::class.java
                )
            }
        return response
    }

    fun toResponse(books: MutableList<BookWithPublisherName?>): MutableList<BookResponse> {
        return books.filterNotNull().map { toResponse(it) }.toMutableList()
    }
}
