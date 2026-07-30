package com.example.demo.api.controller

import com.example.demo.api.BooksOperationApi
import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookPageResponse
import com.example.demo.api.response.BookResponse
import com.example.demo.api.validator.BooksOperationApiControllerValidator
import com.example.demo.config.SearchProperties
import com.example.demo.service.BooksOperationService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Validated
class BooksOperationApiController(
    private val booksOperationService: BooksOperationService,
    private val validator: BooksOperationApiControllerValidator,
    private val searchProperties: SearchProperties
) : BooksOperationApi {
    override fun getBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        return ResponseEntity.ok(booksOperationService.findById(id))
    }

    override fun getBookSearch(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) releaseDateFrom: LocalDate?,
        @RequestParam(required = false) releaseDateTo: LocalDate?,
        @RequestParam @NotNull @Min(0) page: @NotNull @Min(0) Int
    ): BookPageResponse {
        validator.searchValidation(releaseDateFrom, releaseDateTo)
        return booksOperationService.search(keyword, releaseDateFrom, releaseDateTo, page, searchProperties.pageSize)!!
    }

    override fun createBook(@RequestBody @Valid @NotNull request: BookCreateRequest): BookResponse {
        return booksOperationService.create(request)!!
    }

    override fun updateBook(@RequestBody @Valid @NotNull request: BookUpdateRequest): BookResponse {
        return booksOperationService.update(request)!!
    }

    override fun createSalesUnitPrice(
        @PathVariable id: Long,
        @RequestBody @Valid @NotNull request: BookSalesUnitPriceCreateRequest
    ): ResponseEntity<Void> {
        booksOperationService.createSalesUnitPrice(id, request)
        return ResponseEntity.ok().build()
    }

    override fun deleteBook(@PathVariable id: Long) {
        booksOperationService.delete(id)
    }
}
