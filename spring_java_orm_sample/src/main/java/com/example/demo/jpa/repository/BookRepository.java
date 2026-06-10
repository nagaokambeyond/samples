package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Book;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
        SELECT b.id AS id,
               b.title AS title,
               b.author AS author,
               b.releaseDate AS releaseDate,
               b.publisherId AS publisherId,
               p.publisherName AS publisherName,
               b.updateAt AS updateAt,
               b.version AS version
        FROM Book b
        JOIN Publisher p ON b.publisherId = p.id
        ORDER BY b.id
        """)
    List<BookWithPublisherNameProjection> findAllWithPublisherName();

    @Query(value = """
        SELECT b.id AS id,
               b.title AS title,
               b.author AS author,
               b.releaseDate AS releaseDate,
               b.publisherId AS publisherId,
               p.publisherName AS publisherName,
               b.updateAt AS updateAt,
               b.version AS version
        FROM Book b
        JOIN Publisher p ON b.publisherId = p.id
        WHERE b.id = :id
        """)
    Optional<BookWithPublisherNameProjection> findByIdWithPublisherName(@Param("id") Long id);

    @Query(value = """
        SELECT b.id AS id,
               b.title AS title,
               b.author AS author,
               b.releaseDate AS releaseDate,
               b.publisherId AS publisherId,
               p.publisherName AS publisherName,
               b.updateAt AS updateAt,
               b.version AS version
        FROM Book b
        JOIN Publisher p ON b.publisherId = p.id
        WHERE lower(b.title) LIKE lower(concat('%', :keyword, '%'))
          AND (:releaseDateFrom IS NULL OR b.releaseDate >= :releaseDateFrom)
          AND (:releaseDateTo IS NULL OR b.releaseDate <= :releaseDateTo)
        ORDER BY b.id
        """,
        countQuery = """
        SELECT count(b)
        FROM Book b
        WHERE lower(b.title) LIKE lower(concat('%', :keyword, '%'))
          AND (:releaseDateFrom IS NULL OR b.releaseDate >= :releaseDateFrom)
          AND (:releaseDateTo IS NULL OR b.releaseDate <= :releaseDateTo)
        """)
    Page<BookWithPublisherNameProjection> findByTitleContainingIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithWriteLock(@Param("id") Long id);

    interface BookWithPublisherNameProjection {
        Long getId();

        String getTitle();

        String getAuthor();

        LocalDate getReleaseDate();

        Long getPublisherId();

        String getPublisherName();

        LocalDateTime getUpdateAt();

        Long getVersion();
    }
}
