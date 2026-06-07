package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("""
        SELECT b
        FROM Book b
        WHERE lower(b.title) LIKE lower(concat('%', :keyword, '%'))
          AND (:releaseDateFrom IS NULL OR b.releaseDate >= :releaseDateFrom)
          AND (:releaseDateTo IS NULL OR b.releaseDate <= :releaseDateTo)
        ORDER BY b.id
        """)
    List<Book> findByTitleContainingIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithWriteLock(@Param("id") Long id);
}
