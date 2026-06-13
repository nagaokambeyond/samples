package com.example.demo.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_stock", comment = "本在庫", indexes = {
    @Index(name = "idx_book_stock_01", columnList = "book_stock_store_id"),
    @Index(name = "idx_book_stock_02", columnList = "book_stock_book_id"),
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(comment = "本在庫店舗ID")
    @NotNull
    private Long bookStockStoreId;

    @Column(comment = "本在庫本ID")
    @NotNull
    private Long bookStockBookId;

    @Column(comment = "本在庫数量")
    @NotNull
    private Integer bookStockQuantity;

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
}
