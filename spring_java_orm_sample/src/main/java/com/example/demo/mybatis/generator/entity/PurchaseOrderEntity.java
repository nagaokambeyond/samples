package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.PurchaseInvoiceType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   仕入伝票
 */
public class PurchaseOrderEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   仕入伝票種別
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_type")
    private PurchaseInvoiceType purchaseInvoiceType;

    /**
     * Database Column Remarks:
     *   返品元仕入伝票ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.return_purchase_invoice_id")
    private Long returnPurchaseInvoiceId;

    /**
     * Database Column Remarks:
     *   仕入伝票日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_date")
    private LocalDate purchaseInvoiceDate;

    /**
     * Database Column Remarks:
     *   仕入先ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.supplier_id")
    private Long supplierId;

    /**
     * Database Column Remarks:
     *   入庫店舗ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.receiving_store_id")
    private Long receivingStoreId;

    /**
     * Database Column Remarks:
     *   仕入伝票金額
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_amount")
    private Long purchaseInvoiceAmount;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_type")
    public PurchaseInvoiceType getPurchaseInvoiceType() {
        return purchaseInvoiceType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_type")
    public void setPurchaseInvoiceType(PurchaseInvoiceType purchaseInvoiceType) {
        this.purchaseInvoiceType = purchaseInvoiceType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.return_purchase_invoice_id")
    public Long getReturnPurchaseInvoiceId() {
        return returnPurchaseInvoiceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.return_purchase_invoice_id")
    public void setReturnPurchaseInvoiceId(Long returnPurchaseInvoiceId) {
        this.returnPurchaseInvoiceId = returnPurchaseInvoiceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_date")
    public LocalDate getPurchaseInvoiceDate() {
        return purchaseInvoiceDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_date")
    public void setPurchaseInvoiceDate(LocalDate purchaseInvoiceDate) {
        this.purchaseInvoiceDate = purchaseInvoiceDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.supplier_id")
    public Long getSupplierId() {
        return supplierId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.supplier_id")
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.receiving_store_id")
    public Long getReceivingStoreId() {
        return receivingStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.receiving_store_id")
    public void setReceivingStoreId(Long receivingStoreId) {
        this.receivingStoreId = receivingStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_amount")
    public Long getPurchaseInvoiceAmount() {
        return purchaseInvoiceAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.purchase_invoice_amount")
    public void setPurchaseInvoiceAmount(Long purchaseInvoiceAmount) {
        this.purchaseInvoiceAmount = purchaseInvoiceAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_invoice.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}