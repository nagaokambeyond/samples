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
@Table(name = "book_sales_unit_price_history", comment = "本販売単価履歴", indexes = {
    @Index(name = "idx_book_sales_unit_price_history_01", columnList = "book_id"),
    @Index(name = "idx_book_sales_unit_price_history_02", columnList = "effective_from"),
    @Index(name = "idx_book_sales_unit_price_history_03", columnList = "effective_to")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unq_book_sales_unit_price_history_01", columnNames = {"book_id", "effective_from"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesUnitPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, comment = "本ID")
    @NotNull
    private Long bookId;

    @Column(nullable = false, comment = "販売単価")
    @NotNull
    private Integer salesUnitPrice;

    @Column(nullable = false, comment = "有効開始日")
    @NotNull
    private LocalDate effectiveFrom;

    @Column(comment = "有効終了日")
    private LocalDate effectiveTo;

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
