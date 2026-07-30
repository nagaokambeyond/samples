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
@Entity(listener = PurchaseInvoiceDetailListener.class, metamodel = @Metamodel)
@Table(name = "purchase_invoice_detail")
public class PurchaseInvoiceDetail extends AbstractPurchaseInvoiceDetail {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 仕入伝票ID */
    @Column(name = "purchase_invoice_id")
    Long purchaseInvoiceId;

    /** 仕入伝票明細本ID */
    @Column(name = "purchase_invoice_detail_book_id")
    Long purchaseInvoiceDetailBookId;

    /** 仕入伝票明細単価 */
    @Column(name = "purchase_invoice_detail_unit_price")
    Integer purchaseInvoiceDetailUnitPrice;

    /** 仕入伝票明細数量 */
    @Column(name = "purchase_invoice_detail_quantity")
    Integer purchaseInvoiceDetailQuantity;

    /** 仕入伝票明細金額 */
    @Column(name = "purchase_invoice_detail_amount")
    Long purchaseInvoiceDetailAmount;

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
     * Returns the purchaseInvoiceId.
     * 
     * @return the purchaseInvoiceId
     */
    public Long getPurchaseInvoiceId() {
        return purchaseInvoiceId;
    }

    /** 
     * Sets the purchaseInvoiceId.
     * 
     * @param purchaseInvoiceId the purchaseInvoiceId
     */
    public void setPurchaseInvoiceId(Long purchaseInvoiceId) {
        this.purchaseInvoiceId = purchaseInvoiceId;
    }

    /** 
     * Returns the purchaseInvoiceDetailBookId.
     * 
     * @return the purchaseInvoiceDetailBookId
     */
    public Long getPurchaseInvoiceDetailBookId() {
        return purchaseInvoiceDetailBookId;
    }

    /** 
     * Sets the purchaseInvoiceDetailBookId.
     * 
     * @param purchaseInvoiceDetailBookId the purchaseInvoiceDetailBookId
     */
    public void setPurchaseInvoiceDetailBookId(Long purchaseInvoiceDetailBookId) {
        this.purchaseInvoiceDetailBookId = purchaseInvoiceDetailBookId;
    }

    /** 
     * Returns the purchaseInvoiceDetailUnitPrice.
     * 
     * @return the purchaseInvoiceDetailUnitPrice
     */
    public Integer getPurchaseInvoiceDetailUnitPrice() {
        return purchaseInvoiceDetailUnitPrice;
    }

    /** 
     * Sets the purchaseInvoiceDetailUnitPrice.
     * 
     * @param purchaseInvoiceDetailUnitPrice the purchaseInvoiceDetailUnitPrice
     */
    public void setPurchaseInvoiceDetailUnitPrice(Integer purchaseInvoiceDetailUnitPrice) {
        this.purchaseInvoiceDetailUnitPrice = purchaseInvoiceDetailUnitPrice;
    }

    /** 
     * Returns the purchaseInvoiceDetailQuantity.
     * 
     * @return the purchaseInvoiceDetailQuantity
     */
    public Integer getPurchaseInvoiceDetailQuantity() {
        return purchaseInvoiceDetailQuantity;
    }

    /** 
     * Sets the purchaseInvoiceDetailQuantity.
     * 
     * @param purchaseInvoiceDetailQuantity the purchaseInvoiceDetailQuantity
     */
    public void setPurchaseInvoiceDetailQuantity(Integer purchaseInvoiceDetailQuantity) {
        this.purchaseInvoiceDetailQuantity = purchaseInvoiceDetailQuantity;
    }

    /** 
     * Returns the purchaseInvoiceDetailAmount.
     * 
     * @return the purchaseInvoiceDetailAmount
     */
    public Long getPurchaseInvoiceDetailAmount() {
        return purchaseInvoiceDetailAmount;
    }

    /** 
     * Sets the purchaseInvoiceDetailAmount.
     * 
     * @param purchaseInvoiceDetailAmount the purchaseInvoiceDetailAmount
     */
    public void setPurchaseInvoiceDetailAmount(Long purchaseInvoiceDetailAmount) {
        this.purchaseInvoiceDetailAmount = purchaseInvoiceDetailAmount;
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
