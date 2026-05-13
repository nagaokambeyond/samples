package com.example.demo.jpa.service;

import com.example.demo.jpa.entity.Book;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import lombok.NonNull;

import java.util.List;

public interface BookService {
    List<BookResponse> findAll();

    BookResponse findById(@NonNull Long id);

    List<Book> searchByTitle(@NonNull String keyword) ;

    BookResponse create(@NonNull BookCreateRequest request) ;

    BookResponse update(@NonNull BookUpdateRequest request);

    void delete(@NonNull Long id) ;
}
