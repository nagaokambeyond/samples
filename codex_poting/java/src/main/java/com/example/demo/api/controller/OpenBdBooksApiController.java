package com.example.demo.api.controller;

import com.example.demo.api.OpenBdBooksApi;
import com.example.demo.api.response.OpenBdBookResponse;
import com.example.demo.exception.OpenBdBookNotFoundException;
import com.example.demo.openbd.generated.api.BooksApi;
import com.example.demo.openbd.generated.invoker.ApiException;
import com.example.demo.openbd.generated.model.BookDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Validated
public class OpenBdBooksApiController implements OpenBdBooksApi {
    private final BooksApi openBdBooksApi;
    private final ModelMapper modelMapper;

    @Override
    public List<OpenBdBookResponse> getBooksByIsbn(String isbn) throws ApiException {
        final var books = openBdBooksApi.getBooksByIsbn(isbn, null);
        if (Objects.isNull(books) || books.isEmpty() || books.stream().anyMatch(Objects::isNull)) {
            throw new OpenBdBookNotFoundException();
        }

        return books.stream()
            .map(this::toResponse)
            .toList();
    }

    private OpenBdBookResponse toResponse(BookDto book) {
        return modelMapper.map(book, OpenBdBookResponse.class);
    }
}
