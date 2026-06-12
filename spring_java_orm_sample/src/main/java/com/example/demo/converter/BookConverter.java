package com.example.demo.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.jpa.repository.BookRepository.BookWithPublisherNameProjection;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BookConverter {
    public BookResponse toResponse(BookWithPublisherNameProjection book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getPublisherId(), book.getPublisherName(), book.getGenreId(), book.getGenreName(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponse(List<BookWithPublisherNameProjection> books) {
        return books.stream().map(this::toResponse).toList();
    }

    public BookResponse toResponse(com.example.demo.mybatis.entity.BookWithPublisherName book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getPublisherId(), book.getPublisherName(), book.getGenreId(), book.getGenreName(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponseFromMybatisBooks(List<com.example.demo.mybatis.entity.BookWithPublisherName> books) {
        return books.stream().map(this::toResponse).toList();
    }

    public BookResponse toResponse(com.example.demo.doma.entity.BookWithPublisherName book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getReleaseDate(), book.getPublisherId(), book.getPublisherName(), book.getGenreId(), book.getGenreName(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponseFromDomaBooks(List<com.example.demo.doma.entity.BookWithPublisherName> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
