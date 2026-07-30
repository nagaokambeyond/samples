package com.example.demo.api.controller

import com.example.demo.api.OpenBdBooksApi
import com.example.demo.api.response.OpenBdBookResponse
import com.example.demo.exception.OpenBdBookNotFoundException
import com.example.demo.openbd.generated.api.BooksApi
import com.example.demo.openbd.generated.invoker.ApiException
import com.example.demo.openbd.generated.model.BookDto
import org.modelmapper.ModelMapper
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Validated
class OpenBdBooksApiController(private val openBdBooksApi: BooksApi, private val modelMapper: ModelMapper) :
    OpenBdBooksApi {
    @Throws(ApiException::class)
    override fun getBooksByIsbn(isbn: String): List<OpenBdBookResponse?> {
        val books = openBdBooksApi.getBooksByIsbn(isbn, null)
        if (Objects.isNull(books) || books!!.isEmpty() || books.stream()
                .anyMatch { obj: BookDto? -> Objects.isNull(obj) }
        ) {
            throw OpenBdBookNotFoundException()
        }
        return books.stream().map<OpenBdBookResponse?> { book: BookDto? -> this.toResponse(book) }.toList()
    }

    private fun toResponse(book: BookDto?): OpenBdBookResponse? {
        return modelMapper.map<OpenBdBookResponse?>(book, OpenBdBookResponse::class.java)
    }
}
