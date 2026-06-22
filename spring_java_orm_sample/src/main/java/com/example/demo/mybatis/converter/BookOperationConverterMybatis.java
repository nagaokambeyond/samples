package com.example.demo.mybatis.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import org.springframework.stereotype.Component;

import com.example.demo.mybatis.entity.BookWithPublisherName;

import java.util.List;

@Component
public class BookOperationConverterMybatis {
    public BookResponse toResponse(BookWithPublisherName book) {
        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getReleaseDate(),
            book.getPublisherId(),
            book.getPublisherName(),
            book.getGenreId(),
            book.getGenreName(),
            book.getUpdateAt(),
            book.getVersion(),
            book.getBookStockList().stream()
                .map(bookStock -> new BookStockResponse(
                    bookStock.getId(),
                    bookStock.getBookStockStoreId(),
                    bookStock.getStoreName(),
                    bookStock.getBookStockQuantity()
                ))
                .toList()
        );
    }

    public List<BookResponse> toResponse(List<BookWithPublisherName> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
