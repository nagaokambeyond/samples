package com.example.demo.jpa.entity;

import com.example.demo.data.domain.BookStockMovementSourceType;
import com.example.demo.data.domain.BookStockMovementType;
import com.example.demo.jpa.typeconverter.BookStockMovementSourceTypeConverter;
import com.example.demo.jpa.typeconverter.BookStockMovementTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_stock_movement", comment = "本在庫増減履歴", indexes = {
    @Index(name = "idx_book_stock_movement_01", columnList = "store_id, book_id"),
    @Index(name = "idx_book_stock_movement_02", columnList = "movement_date"),
    @Index(name = "idx_book_stock_movement_03", columnList = "movement_type"),
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookStockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", comment = "店舗ID")
    @NotNull
    private Long storeId;

    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_book_stock_movement_01"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;

    @Column(name = "book_id", comment = "本ID")
    @NotNull
    private Long bookId;

    @ManyToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_book_stock_movement_02"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Book book;

    @Column(comment = "在庫増減種別")
    @Convert(converter = BookStockMovementTypeConverter.class)
    @NotNull
    private BookStockMovementType movementType;

    @Column(comment = "増減数量")
    @NotNull
    private Integer quantityDelta;

    @Column(comment = "発生元種別")
    @Convert(converter = BookStockMovementSourceTypeConverter.class)
    private BookStockMovementSourceType sourceType;

    @Column(comment = "発生元ID")
    private Long sourceId;

    @Column(comment = "発生元明細ID")
    private Long sourceDetailId;

    @Column(comment = "在庫増減日付")
    @NotNull
    private LocalDate movementDate;

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
