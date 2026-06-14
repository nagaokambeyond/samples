package com.example.demo.doma.entity;

import lombok.Getter;
import lombok.Setter;
import org.seasar.doma.Association;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BookWithPublisherName {
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "author")
    String author;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "publisher_id")
    Long publisherId;

    @Column(name = "publisher_name")
    String publisherName;

    @Column(name = "genre_id")
    Long genreId;

    @Column(name = "genre_name")
    String genreName;

    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Column(name = "version")
    Long version;

    @Association
    List<BookStockWithStoreName> bookStockList = new ArrayList<>();
}
