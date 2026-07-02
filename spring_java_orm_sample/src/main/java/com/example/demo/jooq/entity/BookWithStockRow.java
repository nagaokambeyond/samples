package com.example.demo.jooq.entity;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class BookWithStockRow {
    Long id;
    String title;
    String author;
    LocalDate releaseDate;
    Long publisherId;
    String publisherName;
    Long genreId;
    String genreName;
    String isbn;
    LocalDateTime updateAt;
    Long version;
    Long bookStockId;
    Long bookStockStoreId;
    String storeName;
    Integer bookStockQuantity;
}
