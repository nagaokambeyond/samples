package com.example.demo.api.controller;


import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.service.BookService;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BooksController {
    private final BookService bookService;

    @GetMapping()
    public ResponseEntity<List<BookResponse>> getBookAll() {
        return new ResponseEntity<>(bookService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(
        @PathVariable Long id
    ) {
        return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<Book> getBook(
        @RequestParam @NotBlank  String title
    ) {
        return bookService.searchByTitle(title);
    }

    @PostMapping("/create")
    public BookResponse createBook(
        @RequestBody @Valid @NotNull BookCreateRequest request
    ) {
        return bookService.create(request);
    }

    @PostMapping("/update")
    public BookResponse updateBook(
        @RequestBody @Valid @NotNull BookUpdateRequest request
    ) {
        return bookService.update(request);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(
        @PathVariable Long id
    ) {
        bookService.delete(id);
    }
}
