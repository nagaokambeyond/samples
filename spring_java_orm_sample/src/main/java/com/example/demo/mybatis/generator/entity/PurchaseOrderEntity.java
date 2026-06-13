package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.PurchaseOrderType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   仕入伝票
 */
public class PurchaseOrderEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   仕入伝票種別
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_type")
    private PurchaseOrderType purchaseOrderType;

    /**
     * Database Column Remarks:
     *   返品元仕入伝票ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.return_purchase_order_id")
    private Long returnPurchaseOrderId;

    /**
     * Database Column Remarks:
     *   仕入伝票日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_date")
    private LocalDate purchaseOrderDate;

    /**
     * Database Column Remarks:
     *   仕入先ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.supplier_id")
    private Long supplierId;

    /**
     * Database Column Remarks:
     *   入庫店舗ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.receiving_store_id")
    private Long receivingStoreId;

    /**
     * Database Column Remarks:
     *   仕入伝票金額
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_amount")
    private Long purchaseOrderAmount;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_type")
    public PurchaseOrderType getPurchaseOrderType() {
        return purchaseOrderType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_type")
    public void setPurchaseOrderType(PurchaseOrderType purchaseOrderType) {
        this.purchaseOrderType = purchaseOrderType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.return_purchase_order_id")
    public Long getReturnPurchaseOrderId() {
        return returnPurchaseOrderId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.return_purchase_order_id")
    public void setReturnPurchaseOrderId(Long returnPurchaseOrderId) {
        this.returnPurchaseOrderId = returnPurchaseOrderId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_date")
    public LocalDate getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_date")
    public void setPurchaseOrderDate(LocalDate purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.supplier_id")
    public Long getSupplierId() {
        return supplierId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.supplier_id")
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.receiving_store_id")
    public Long getReceivingStoreId() {
        return receivingStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.receiving_store_id")
    public void setReceivingStoreId(Long receivingStoreId) {
        this.receivingStoreId = receivingStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_amount")
    public Long getPurchaseOrderAmount() {
        return purchaseOrderAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.purchase_order_amount")
    public void setPurchaseOrderAmount(Long purchaseOrderAmount) {
        this.purchaseOrderAmount = purchaseOrderAmount;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: purchase_order.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}