package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Book;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
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
               b.release_date AS releaseDate,
               b.publisher_id AS publisherId,
               p.publisher_name AS publisherName,
               b.genre_id AS genreId,
               g.genre_name AS genreName,
               b.update_at AS updateAt,
               b.version AS version,
               bs.id AS bookStockId,
               bs.book_stock_store_id AS bookStockStoreId,
               s.store_name AS storeName,
               bs.book_stock_quantity AS bookStockQuantity
        FROM book b
        JOIN publisher p ON b.publisher_id = p.id
        JOIN book_genre g ON b.genre_id = g.id
        LEFT JOIN book_stock bs ON bs.book_stock_book_id = b.id
        LEFT JOIN store s ON s.id = bs.book_stock_store_id
        WHERE b.id = :id
        ORDER BY b.id, bs.id
        """, nativeQuery = true)
    List<BookWithStockRowProjection> findByIdWithPublisherName(@Param("id") Long id);

    @Query(value = """
        WITH paged_book AS (
            SELECT b.id,
                   b.title,
                   b.author,
                   b.release_date,
                   b.publisher_id,
                   p.publisher_name,
                   b.genre_id,
                   g.genre_name,
                   b.update_at,
                   b.version
            FROM book b
            JOIN publisher p ON b.publisher_id = p.id
            JOIN book_genre g ON b.genre_id = g.id
            WHERE (:keyword IS NULL OR trim(:keyword) = ''
                   OR lower(b.title) LIKE lower(concat(:keyword, '%'))
                   OR lower(b.author) LIKE lower(concat(:keyword, '%')))
              AND (:releaseDateFrom IS NULL OR b.release_date >= :releaseDateFrom)
              AND (:releaseDateTo IS NULL OR b.release_date <= :releaseDateTo)
            ORDER BY b.id
            LIMIT :limit
            OFFSET :offsetValue
        )
        SELECT b.id AS id,
               b.title AS title,
               b.author AS author,
               b.release_date AS releaseDate,
               b.publisher_id AS publisherId,
               b.publisher_name AS publisherName,
               b.genre_id AS genreId,
               b.genre_name AS genreName,
               b.update_at AS updateAt,
               b.version AS version,
               bs.id AS bookStockId,
               bs.book_stock_store_id AS bookStockStoreId,
               s.store_name AS storeName,
               bs.book_stock_quantity AS bookStockQuantity
        FROM paged_book b
        LEFT JOIN book_stock bs ON bs.book_stock_book_id = b.id
        LEFT JOIN store s ON s.id = bs.book_stock_store_id
        ORDER BY b.id, bs.id
        """, nativeQuery = true)
    List<BookWithStockRowProjection> findByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo,
        @Param("limit") int limit,
        @Param("offsetValue") long offset
    );

    @Query(value = """
        SELECT count(*)
        FROM book b
        WHERE (:keyword IS NULL OR trim(:keyword) = ''
               OR lower(b.title) LIKE lower(concat(:keyword, '%'))
               OR lower(b.author) LIKE lower(concat(:keyword, '%')))
          AND (:releaseDateFrom IS NULL OR b.release_date >= :releaseDateFrom)
          AND (:releaseDateTo IS NULL OR b.release_date <= :releaseDateTo)
        """, nativeQuery = true)
    long countByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithWriteLock(@Param("id") Long id);

    interface BookWithStockRowProjection {
        Long getId();

        String getTitle();

        String getAuthor();

        LocalDate getReleaseDate();

        Long getPublisherId();

        String getPublisherName();

        Long getGenreId();

        String getGenreName();

        LocalDateTime getUpdateAt();

        Long getVersion();

        Long getBookStockId();

        Long getBookStockStoreId();

        String getStoreName();

        Integer getBookStockQuantity();
    }
}
