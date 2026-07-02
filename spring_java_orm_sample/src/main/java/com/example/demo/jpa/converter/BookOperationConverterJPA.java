package com.example.demo.jpa.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import com.example.demo.jpa.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Component
@Profile("jpa")
@RequiredArgsConstructor
public class BookOperationConverterJPA {
    private final ModelMapper modelMapper;

    public BookResponse toResponseFrom(List<BookRepository.BookWithStockRowProjection> rows) {
        return toResponse(rows).getFirst();
    }

    public List<BookResponse> toResponse(List<BookRepository.BookWithStockRowProjection> rows) {
        final var responses = new LinkedHashMap<Long, BookOperationConverterJPA.BookResponseBuilder>();
        for (final var row : rows) {
            final var builder = responses.computeIfAbsent(row.getId(), id -> new BookOperationConverterJPA.BookResponseBuilder(row));
            if (Objects.nonNull(row.getBookStockId())) {
                final var bookStock = modelMapper.map(row, BookStockResponse.class);
                bookStock.setId(row.getBookStockId());
                builder.getBookStockList().add(bookStock);
            }
        }
        return responses.values().stream().map(this::build).toList();
    }

    private BookResponse build(BookResponseBuilder builder) {
        final var response = modelMapper.map(builder, BookResponse.class);
        response.setBookStockList(builder.getBookStockList());
        return response;
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
        String isbn;
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
                row.getIsbn(),
                row.getUpdateAt(),
                row.getVersion(),
                new ArrayList<>()
            );
        }

        private BookResponseBuilder(
            Long id,
            String title,
            String author,
            LocalDate releaseDate,
            Long publisherId,
            String publisherName,
            Long genreId,
            String genreName,
            String isbn,
            LocalDateTime updateAt,
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
            this.isbn = isbn;
            this.updateAt = updateAt;
            this.version = version;
            this.bookStockList = bookStockList;
        }

    }
}
