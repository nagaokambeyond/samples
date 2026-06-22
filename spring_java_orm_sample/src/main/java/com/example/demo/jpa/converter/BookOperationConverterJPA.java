package com.example.demo.jpa.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import com.example.demo.jpa.repository.BookRepository;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Component
public class BookOperationConverterJPA {
    public BookResponse toResponseFromJpaRows(List<BookRepository.BookWithStockRowProjection> rows) {
        return toResponseListFromJpaRows(rows).getFirst();
    }

    public List<BookResponse> toResponseListFromJpaRows(List<BookRepository.BookWithStockRowProjection> rows) {
        final var responses = new LinkedHashMap<Long, BookOperationConverterJPA.BookResponseBuilder>();
        for (final var row : rows) {
            final var builder = responses.computeIfAbsent(row.getId(), id -> new BookOperationConverterJPA.BookResponseBuilder(row));
            if (Objects.nonNull(row.getBookStockId())) {
                builder.getBookStockList().add(new BookStockResponse(
                    row.getBookStockId(),
                    row.getBookStockStoreId(),
                    row.getStoreName(),
                    row.getBookStockQuantity()
                ));
            }
        }
        return responses.values().stream().map(BookOperationConverterJPA.BookResponseBuilder::build).toList();
    }

    @Value
    private static class BookResponseBuilder {
        Long id;
        String title;
        String author;
        java.time.LocalDate releaseDate;
        Long publisherId;
        String publisherName;
        Long genreId;
        String genreName;
        java.time.LocalDateTime updateAt;
        Long version;
        List<BookStockResponse> bookStockList;

        private BookResponseBuilder(BookRepository.BookWithStockRowProjection row) {
            this(
                row.getId(),
                row.getTitle(),
                row.getAuthor(),
                row.getReleaseDate(),
                row.getPublisherId(),
                row.getPublisherName(),
                row.getGenreId(),
                row.getGenreName(),
                row.getUpdateAt(),
                row.getVersion(),
                new ArrayList<>()
            );
        }

        private BookResponseBuilder(
            Long id,
            String title,
            String author,
            java.time.LocalDate releaseDate,
            Long publisherId,
            String publisherName,
            Long genreId,
            String genreName,
            java.time.LocalDateTime updateAt,
            Long version,
            List<BookStockResponse> bookStockList
        ) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.releaseDate = releaseDate;
            this.publisherId = publisherId;
            this.publisherName = publisherName;
            this.genreId = genreId;
            this.genreName = genreName;
            this.updateAt = updateAt;
            this.version = version;
            this.bookStockList = bookStockList;
        }

        private BookResponse build() {
            return new BookResponse(
                id,
                title,
                author,
                releaseDate,
                publisherId,
                publisherName,
                genreId,
                genreName,
                updateAt,
                version,
                bookStockList
            );
        }
    }
}
