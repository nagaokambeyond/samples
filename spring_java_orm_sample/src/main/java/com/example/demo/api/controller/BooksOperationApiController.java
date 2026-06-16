package com.example.demo.api.controller;


import com.example.demo.api.BooksOperationApi;
import com.example.demo.config.SearchProperties;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.validator.BooksOperationApiControllerValidator;
import com.example.demo.service.BooksOperationService;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
public class BooksOperationApiController implements BooksOperationApi {
    private final BooksOperationService booksOperationService;
    private final BooksOperationApiControllerValidator validator;
    private final SearchProperties searchProperties;

    @Override
    public ResponseEntity<BookResponse> getBook(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(booksOperationService.findById(id));
    }

    @Override
    public BookPageResponse getBookSearch(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) LocalDate releaseDateFrom,
        @RequestParam(required = false) LocalDate releaseDateTo,
        @RequestParam @NotNull @Min(0) Integer page
    ) {
        validator.searchValidation(releaseDateFrom, releaseDateTo);
        return booksOperationService.search(keyword, releaseDateFrom, releaseDateTo, page, searchProperties.getPageSize());
    }

    @Override
    public BookResponse createBook(
        @RequestBody @Valid @NotNull BookCreateRequest request
    ) {
        return booksOperationService.create(request);
    }

    @Override
    public BookResponse updateBook(
        @RequestBody @Valid @NotNull BookUpdateRequest request
    ) {
        return booksOperationService.update(request);
    }

    @Override
    public void deleteBook(
        @PathVariable Long id
    ) {
        booksOperationService.delete(id);
    }
}
