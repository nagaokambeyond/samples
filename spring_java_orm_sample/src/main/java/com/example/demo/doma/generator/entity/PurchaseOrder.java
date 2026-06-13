package com.example.demo.doma.generator.entity;

import com.example.demo.data.domain.PurchaseOrderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 
 */
@Entity(listener = PurchaseOrderListener.class, metamodel = @Metamodel)
@Table(name = "purchase_order")
public class PurchaseOrder extends AbstractPurchaseOrder {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 仕入伝票種別 */
    @Column(name = "purchase_order_type")
    PurchaseOrderType purchaseOrderType;

    /** 返品元仕入伝票ID */
    @Column(name = "return_purchase_order_id")
    Long returnPurchaseOrderId;

    /** 仕入伝票日付 */
    @Column(name = "purchase_order_date")
    LocalDate purchaseOrderDate;

    /** 仕入先ID */
    @Column(name = "supplier_id")
    Long supplierId;

    /** 入庫店舗ID */
    @Column(name = "receiving_store_id")
    Long receivingStoreId;

    /** 仕入伝票金額 */
    @Column(name = "purchase_order_amount")
    Long purchaseOrderAmount;

    /** 作成日時 */
    @Column(name = "create_at")
    LocalDateTime createAt;

    /** 更新日時 */
    @Column(name = "update_at")
    LocalDateTime updateAt;

    /** バージョン */
    @Version
    @Column(name = "version")
    Long version;

    /** 
     * Returns the id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /** 
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** 
     * Returns the purchaseOrderType.
     * 
     * @return the purchaseOrderType
     */
    public PurchaseOrderType getPurchaseOrderType() {
        return purchaseOrderType;
    }

    /** 
     * Sets the purchaseOrderType.
     * 
     * @param purchaseOrderType the purchaseOrderType
     */
    public void setPurchaseOrderType(PurchaseOrderType purchaseOrderType) {
        this.purchaseOrderType = purchaseOrderType;
    }

    /** 
     * Returns the returnPurchaseOrderId.
     * 
     * @return the returnPurchaseOrderId
     */
    public Long getReturnPurchaseOrderId() {
        return returnPurchaseOrderId;
    }

    /** 
     * Sets the returnPurchaseOrderId.
     * 
     * @param returnPurchaseOrderId the returnPurchaseOrderId
     */
    public void setReturnPurchaseOrderId(Long returnPurchaseOrderId) {
        this.returnPurchaseOrderId = returnPurchaseOrderId;
    }

    /** 
     * Returns the purchaseOrderDate.
     * 
     * @return the purchaseOrderDate
     */
    public LocalDate getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    /** 
     * Sets the purchaseOrderDate.
     * 
     * @param purchaseOrderDate the purchaseOrderDate
     */
    public void setPurchaseOrderDate(LocalDate purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    /** 
     * Returns the supplierId.
     * 
     * @return the supplierId
     */
    public Long getSupplierId() {
        return supplierId;
    }

    /** 
     * Sets the supplierId.
     * 
     * @param supplierId the supplierId
     */
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    /** 
     * Returns the receivingStoreId.
     * 
     * @return the receivingStoreId
     */
    public Long getReceivingStoreId() {
        return receivingStoreId;
    }

    /** 
     * Sets the receivingStoreId.
     * 
     * @param receivingStoreId the receivingStoreId
     */
    public void setReceivingStoreId(Long receivingStoreId) {
        this.receivingStoreId = receivingStoreId;
    }

    /** 
     * Returns the purchaseOrderAmount.
     * 
     * @return the purchaseOrderAmount
     */
    public Long getPurchaseOrderAmount() {
        return purchaseOrderAmount;
    }

    /** 
     * Sets the purchaseOrderAmount.
     * 
     * @param purchaseOrderAmount the purchaseOrderAmount
     */
    public void setPurchaseOrderAmount(Long purchaseOrderAmount) {
        this.purchaseOrderAmount = purchaseOrderAmount;
    }

    /** 
     * Returns the createAt.
     * 
     * @return the createAt
     */
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    /** 
     * Sets the createAt.
     * 
     * @param createAt the createAt
     */
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    /** 
     * Returns the updateAt.
     * 
     * @return the updateAt
     */
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    /** 
     * Sets the updateAt.
     * 
     * @param updateAt the updateAt
     */
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    /** 
     * Returns the version.
     * 
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /** 
     * Sets the version.
     * 
     * @param version the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
