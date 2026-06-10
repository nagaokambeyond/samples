package com.example.demo.api.controller;


import com.example.demo.api.BookApi;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.validator.BookApiControllerValidator;
import com.example.demo.service.BookService;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class BooksApiController implements BookApi {
    private final BookService bookService;
    private final BookApiControllerValidator validator;

    @Override
    public ResponseEntity<List<BookResponse>> getBookAll() {
        return new ResponseEntity<>(bookService.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookResponse> getBook(
        @PathVariable Long id
    ) {
        return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
    }

    @Override
    public BookPageResponse getBookSearch(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) LocalDate releaseDateFrom,
        @RequestParam(required = false) LocalDate releaseDateTo,
        @RequestParam @NotNull @Min(0) Integer page,
        @RequestParam @NotNull @Min(1) Integer size
    ) {
        validator.searchValidation(releaseDateFrom, releaseDateTo);
        return bookService.search(title, releaseDateFrom, releaseDateTo, page, size);
    }

    @Override
    public BookResponse createBook(
        @RequestBody @Valid @NotNull BookCreateRequest request
    ) {
        return bookService.create(request);
    }

    @Override
    public BookResponse updateBook(
        @RequestBody @Valid @NotNull BookUpdateRequest request
    ) {
        return bookService.update(request);
    }

    @Override
    public void deleteBook(
        @PathVariable Long id
    ) {
        bookService.delete(id);
    }
}
