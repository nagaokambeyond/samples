package com.example.demo.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    List<BookResponse> findAll();

    BookResponse findById(@NonNull Long id);

    List<BookResponse> search(@NonNull String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo) ;

    BookResponse create(@NonNull BookCreateRequest request) ;

    BookResponse update(@NonNull BookUpdateRequest request);

    void delete(@NonNull Long id) ;
}
