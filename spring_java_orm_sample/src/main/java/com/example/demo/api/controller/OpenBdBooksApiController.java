package com.example.demo.api.controller;

import com.example.demo.api.OpenBdBooksApi;
import com.example.demo.openbd.generated.api.BooksApi;
import com.example.demo.openbd.generated.invoker.ApiException;
import com.example.demo.openbd.generated.model.BookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class OpenBdBooksApiController implements OpenBdBooksApi {
    private final BooksApi openBdBooksApi;

    @Override
    public List<BookDto> getBooksByIsbn(String isbn) throws ApiException {
        return openBdBooksApi.getBooksByIsbn(isbn, null);
    }
}
