package com.example.demo.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import lombok.NonNull;

import java.time.LocalDate;

public interface BookService {
    BookResponse findById(@NonNull Long id);

    BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) ;

    BookResponse create(@NonNull BookCreateRequest request) ;

    BookResponse update(@NonNull BookUpdateRequest request);

    void delete(@NonNull Long id) ;
}
