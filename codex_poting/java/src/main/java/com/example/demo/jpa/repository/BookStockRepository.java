package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.BookStock;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookStockRepository extends JpaRepository<BookStock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    @Query("""
        SELECT bs
        FROM BookStock bs
        WHERE bs.bookStockStoreId = :storeId
          AND bs.bookStockBookId = :bookId
        """)
    Optional<BookStock> findByStoreIdAndBookIdWithWriteLock(
        @Param("storeId") Long storeId,
        @Param("bookId") Long bookId
    );
}
