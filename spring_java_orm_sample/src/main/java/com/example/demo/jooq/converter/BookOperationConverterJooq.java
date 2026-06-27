package com.example.demo.jooq.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import com.example.demo.jooq.entity.BookWithStockRow;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Profile("jooq")
public class BookOperationConverterJooq {
    public BookResponse toResponse(List<BookWithStockRow> rows) {
        return toResponseList(rows).getFirst();
    }

    public List<BookResponse> toResponseList(List<BookWithStockRow> rows) {
        final var responses = new LinkedHashMap<Long, BookResponse>();
        rows.forEach(row -> {
            final var response = responses.computeIfAbsent(row.getId(), ignored -> toBookResponse(row));
            if (row.getBookStockId() != null) {
                response.getBookStockList().add(toBookStockResponse(row));
            }
        });
        return new ArrayList<>(responses.values());
    }

    private BookResponse toBookResponse(BookWithStockRow row) {
        final var response = new BookResponse();
        response.setId(row.getId());
        response.setTitle(row.getTitle());
        response.setAuthor(row.getAuthor());
        response.setReleaseDate(row.getReleaseDate());
        response.setPublisherId(row.getPublisherId());
        response.setPublisherName(row.getPublisherName());
        response.setGenreId(row.getGenreId());
        response.setGenreName(row.getGenreName());
        response.setUpdateAt(row.getUpdateAt());
        response.setVersion(row.getVersion());
        response.setBookStockList(new ArrayList<>());
        return response;
    }

    private BookStockResponse toBookStockResponse(BookWithStockRow row) {
        final var response = new BookStockResponse();
        response.setId(row.getBookStockId());
        response.setBookStockStoreId(row.getBookStockStoreId());
        response.setStoreName(row.getStoreName());
        response.setBookStockQuantity(row.getBookStockQuantity());
        return response;
    }
}
