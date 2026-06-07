package com.example.demo.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.jpa.entity.Book;
import com.example.demo.mybatis.generator.entity.BookEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BookConverter {
    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponse(List<Book> books) {
        return books.stream().map(book-> new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getUpdateAt(), book.getVersion())).toList();
    }

    public BookResponse toResponse(BookEntity book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponseFromBookEntities(List<BookEntity> books) {
        return books.stream().map(this::toResponse).toList();
    }

    public BookResponse toResponse(com.example.demo.doma.generator.entity.Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(),book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponseFromDomaBooks(List<com.example.demo.doma.generator.entity.Book> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
