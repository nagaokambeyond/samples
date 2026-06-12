package com.example.demo.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book", comment = "本", indexes ={
    @Index(name = "idx_book_01", columnList = "release_date"),
    @Index(name = "idx_book_02", columnList = "publisher_id"),
    @Index(name = "idx_book_03", columnList = "genre_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, comment = "タイトル")
    private String title;

    @Column(length = 200, comment = "著者")
    private String author;

    @Column(comment = "発売日付")
    @NotNull
    private LocalDate releaseDate;

    @Column(nullable = false, comment = "出版社ID")
    @NotNull
    private Long publisherId;

    @Column(nullable = false, comment = "ジャンルID")
    @NotNull
    private Long genreId;

    @Column(comment = "作成日時")
    @CreatedDate
    @NotNull
    private LocalDateTime createAt;

    @Column(comment = "更新日時")
    @LastModifiedDate
    @NotNull
    private LocalDateTime updateAt;

    @Column(nullable = false, comment = "バージョン")
    @NotNull
    @PositiveOrZero
    @Version
    private Long version = 1L;

    public Book(
        Long id,
        String title,
        String author,
        LocalDate releaseDate,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        Long version
    ) {
        this(id, title, author, releaseDate, 1L, 5L, createAt, updateAt, version);
    }
}
