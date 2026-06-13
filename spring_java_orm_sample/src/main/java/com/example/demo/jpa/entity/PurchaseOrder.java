package com.example.demo.jpa.entity;

import com.example.demo.data.domain.PurchaseOrderType;
import com.example.demo.jpa.typeconverter.PurchaseOrderTypeConverter;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order", comment = "仕入伝票", indexes ={
    @Index(name = "idx_purchase_order_01", columnList = "purchase_order_type"),
    @Index(name = "idx_purchase_order_02", columnList = "return_purchase_order_id"),
    @Index(name = "idx_purchase_order_03", columnList = "purchase_order_date"),
    @Index(name = "idx_purchase_order_04", columnList = "supplier_id"),
    @Index(name = "idx_purchase_order_05", columnList = "receiving_store_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(comment = "仕入伝票種別")
    @Convert(converter = PurchaseOrderTypeConverter.class)
    @NotNull
    private PurchaseOrderType purchaseOrderType;

    @Column(comment = "返品元仕入伝票ID")
    private Long returnPurchaseOrderId;

    @Column(comment = "仕入伝票日付")
    @NotNull
    private LocalDate purchaseOrderDate;

    @Column(comment = "仕入先ID")
    @NotNull
    private Long supplierId;

    @Column(comment = "入庫店舗ID")
    @NotNull
    private Long receivingStoreId;

    @Column(comment = "仕入伝票金額")
    @NotNull
    private Long purchaseOrderAmount;

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
