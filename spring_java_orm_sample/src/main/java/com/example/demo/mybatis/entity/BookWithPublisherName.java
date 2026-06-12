package com.example.demo.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookWithPublisherName {
    private Long id;
    private String title;
    private String author;
    private LocalDate releaseDate;
    private Long publisherId;
    private String publisherName;
    private Long genreId;
    private String genreName;
    private LocalDateTime updateAt;
    private Long version;
}
