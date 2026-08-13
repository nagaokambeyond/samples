package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.BookSalesUnitPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookSalesUnitPriceHistoryRepository extends JpaRepository<BookSalesUnitPriceHistory, Long> {
    @Query(value = "SELECT coalesce(max(id), 0) + 1 FROM book_sales_unit_price_history", nativeQuery = true)
    Long nextId();

    @Modifying
    @Query(value = """
        INSERT INTO book_sales_unit_price_history
            (id, book_id, sales_unit_price, effective_from, effective_to, create_at, update_at, version)
        VALUES
            (:id, :bookId, :salesUnitPrice, :effectiveFrom, :effectiveTo, :now, :now, 1)
        """, nativeQuery = true)
    void insertWithId(
        @Param("id") Long id,
        @Param("bookId") Long bookId,
        @Param("salesUnitPrice") Integer salesUnitPrice,
        @Param("effectiveFrom") LocalDate effectiveFrom,
        @Param("effectiveTo") LocalDate effectiveTo,
        @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT h
        FROM BookSalesUnitPriceHistory h
        WHERE h.bookId = :bookId
          AND h.effectiveFrom = (
              SELECT max(prev.effectiveFrom)
              FROM BookSalesUnitPriceHistory prev
              WHERE prev.bookId = :bookId
                AND prev.effectiveFrom < :effectiveFrom
          )
        """)
    Optional<BookSalesUnitPriceHistory> findPreviousHistory(
        @Param("bookId") Long bookId,
        @Param("effectiveFrom") LocalDate effectiveFrom
    );

    List<BookSalesUnitPriceHistory> findByBookIdAndEffectiveFromGreaterThanEqualOrderByEffectiveFrom(
        Long bookId,
        LocalDate effectiveFrom
    );
}
