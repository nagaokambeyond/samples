package com.example.demo.mybatis.converter

import com.example.demo.api.response.BookResponse
import com.example.demo.api.response.BookStockResponse
import com.example.demo.mybatis.entity.BookStockWithStoreName
import com.example.demo.mybatis.entity.BookWithPublisherName
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("mybatis")
class BookOperationConverterMybatis(private val modelMapper: ModelMapper) {
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
