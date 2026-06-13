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
@Table(name = "purchase_order_detail", comment = "仕入伝票明細", indexes ={
    @Index(name = "idx_purchase_order_detail_01", columnList = "purchase_order_id"),
    @Index(name = "idx_purchase_order_detail_02", columnList = "purchase_order_detail_book_id"),
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(comment = "仕入伝票ID")
    @NotNull
    private Long purchaseOrderId;

    @Column(comment = "仕入伝票明細本ID")
    @NotNull
    private Long purchaseOrderDetailBookId;

    @Column(comment = "仕入伝票明細単価")
    @NotNull
    private Integer purchaseOrderDetailUnitPrice;

    @Column(comment = "仕入伝票明細数量")
    @NotNull
    private Integer purchaseOrderDetailQuantity;

    @Column(comment = "仕入伝票明細金額")
    @NotNull
    private Long purchaseOrderDetailAmount;

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
