package com.example.demo.doma.generator.entity;

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
@Entity(listener = PurchaseOrderDetailListener.class, metamodel = @Metamodel)
@Table(name = "purchase_order_detail")
public class PurchaseOrderDetail extends AbstractPurchaseOrderDetail {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 仕入伝票ID */
    @Column(name = "purchase_order_id")
    Long purchaseOrderId;

    /** 仕入伝票明細本ID */
    @Column(name = "purchase_order_detail_book_id")
    Long purchaseOrderDetailBookId;

    /** 仕入伝票明細単価 */
    @Column(name = "purchase_order_detail_unit_price")
    Integer purchaseOrderDetailUnitPrice;

    /** 仕入伝票明細数量 */
    @Column(name = "purchase_order_detail_quantity")
    Integer purchaseOrderDetailQuantity;

    /** 仕入伝票明細金額 */
    @Column(name = "purchase_order_detail_amount")
    Long purchaseOrderDetailAmount;

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
     * Returns the purchaseOrderId.
     * 
     * @return the purchaseOrderId
     */
    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    /** 
     * Sets the purchaseOrderId.
     * 
     * @param purchaseOrderId the purchaseOrderId
     */
    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    /** 
     * Returns the purchaseOrderDetailBookId.
     * 
     * @return the purchaseOrderDetailBookId
     */
    public Long getPurchaseOrderDetailBookId() {
        return purchaseOrderDetailBookId;
    }

    /** 
     * Sets the purchaseOrderDetailBookId.
     * 
     * @param purchaseOrderDetailBookId the purchaseOrderDetailBookId
     */
    public void setPurchaseOrderDetailBookId(Long purchaseOrderDetailBookId) {
        this.purchaseOrderDetailBookId = purchaseOrderDetailBookId;
    }

    /** 
     * Returns the purchaseOrderDetailUnitPrice.
     * 
     * @return the purchaseOrderDetailUnitPrice
     */
    public Integer getPurchaseOrderDetailUnitPrice() {
        return purchaseOrderDetailUnitPrice;
    }

    /** 
     * Sets the purchaseOrderDetailUnitPrice.
     * 
     * @param purchaseOrderDetailUnitPrice the purchaseOrderDetailUnitPrice
     */
    public void setPurchaseOrderDetailUnitPrice(Integer purchaseOrderDetailUnitPrice) {
        this.purchaseOrderDetailUnitPrice = purchaseOrderDetailUnitPrice;
    }

    /** 
     * Returns the purchaseOrderDetailQuantity.
     * 
     * @return the purchaseOrderDetailQuantity
     */
    public Integer getPurchaseOrderDetailQuantity() {
        return purchaseOrderDetailQuantity;
    }

    /** 
     * Sets the purchaseOrderDetailQuantity.
     * 
     * @param purchaseOrderDetailQuantity the purchaseOrderDetailQuantity
     */
    public void setPurchaseOrderDetailQuantity(Integer purchaseOrderDetailQuantity) {
        this.purchaseOrderDetailQuantity = purchaseOrderDetailQuantity;
    }

    /** 
     * Returns the purchaseOrderDetailAmount.
     * 
     * @return the purchaseOrderDetailAmount
     */
    public Long getPurchaseOrderDetailAmount() {
        return purchaseOrderDetailAmount;
    }

    /** 
     * Sets the purchaseOrderDetailAmount.
     * 
     * @param purchaseOrderDetailAmount the purchaseOrderDetailAmount
     */
    public void setPurchaseOrderDetailAmount(Long purchaseOrderDetailAmount) {
        this.purchaseOrderDetailAmount = purchaseOrderDetailAmount;
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
