package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   仕入伝票明細
 */
public class PurchaseOrderDetailEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   仕入伝票ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_id")
    private Long purchaseInvoiceId;

    /**
     * Database Column Remarks:
     *   仕入伝票明細本ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_book_id")
    private Long purchaseInvoiceDetailBookId;

    /**
     * Database Column Remarks:
     *   仕入伝票明細単価
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_unit_price")
    private Integer purchaseInvoiceDetailUnitPrice;

    /**
     * Database Column Remarks:
     *   仕入伝票明細数量
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_quantity")
    private Integer purchaseInvoiceDetailQuantity;

    /**
     * Database Column Remarks:
     *   仕入伝票明細金額
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_amount")
    private Long purchaseInvoiceDetailAmount;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_id")
    public Long getPurchaseInvoiceId() {
        return purchaseInvoiceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_id")
    public void setPurchaseInvoiceId(Long purchaseInvoiceId) {
        this.purchaseInvoiceId = purchaseInvoiceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_book_id")
    public Long getPurchaseInvoiceDetailBookId() {
        return purchaseInvoiceDetailBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_book_id")
    public void setPurchaseInvoiceDetailBookId(Long purchaseInvoiceDetailBookId) {
        this.purchaseInvoiceDetailBookId = purchaseInvoiceDetailBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_unit_price")
    public Integer getPurchaseInvoiceDetailUnitPrice() {
        return purchaseInvoiceDetailUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_unit_price")
    public void setPurchaseInvoiceDetailUnitPrice(Integer purchaseInvoiceDetailUnitPrice) {
        this.purchaseInvoiceDetailUnitPrice = purchaseInvoiceDetailUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_quantity")
    public Integer getPurchaseInvoiceDetailQuantity() {
        return purchaseInvoiceDetailQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_quantity")
    public void setPurchaseInvoiceDetailQuantity(Integer purchaseInvoiceDetailQuantity) {
        this.purchaseInvoiceDetailQuantity = purchaseInvoiceDetailQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_amount")
    public Long getPurchaseInvoiceDetailAmount() {
        return purchaseInvoiceDetailAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.purchase_invoice_detail_amount")
    public void setPurchaseInvoiceDetailAmount(Long purchaseInvoiceDetailAmount) {
        this.purchaseInvoiceDetailAmount = purchaseInvoiceDetailAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice_detail.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}