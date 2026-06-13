package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   仕入伝票明細
 */
public class PurchaseOrderDetailEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   仕入伝票ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_id")
    private Long purchaseOrderId;

    /**
     * Database Column Remarks:
     *   仕入伝票明細本ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_book_id")
    private Long purchaseOrderDetailBookId;

    /**
     * Database Column Remarks:
     *   仕入伝票明細単価
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_unit_price")
    private Integer purchaseOrderDetailUnitPrice;

    /**
     * Database Column Remarks:
     *   仕入伝票明細数量
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_quantity")
    private Integer purchaseOrderDetailQuantity;

    /**
     * Database Column Remarks:
     *   仕入伝票明細金額
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_amount")
    private Long purchaseOrderDetailAmount;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_id")
    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_id")
    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_book_id")
    public Long getPurchaseOrderDetailBookId() {
        return purchaseOrderDetailBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_book_id")
    public void setPurchaseOrderDetailBookId(Long purchaseOrderDetailBookId) {
        this.purchaseOrderDetailBookId = purchaseOrderDetailBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_unit_price")
    public Integer getPurchaseOrderDetailUnitPrice() {
        return purchaseOrderDetailUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_unit_price")
    public void setPurchaseOrderDetailUnitPrice(Integer purchaseOrderDetailUnitPrice) {
        this.purchaseOrderDetailUnitPrice = purchaseOrderDetailUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_quantity")
    public Integer getPurchaseOrderDetailQuantity() {
        return purchaseOrderDetailQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_quantity")
    public void setPurchaseOrderDetailQuantity(Integer purchaseOrderDetailQuantity) {
        this.purchaseOrderDetailQuantity = purchaseOrderDetailQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_amount")
    public Long getPurchaseOrderDetailAmount() {
        return purchaseOrderDetailAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.purchase_order_detail_amount")
    public void setPurchaseOrderDetailAmount(Long purchaseOrderDetailAmount) {
        this.purchaseOrderDetailAmount = purchaseOrderDetailAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order_detail.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}