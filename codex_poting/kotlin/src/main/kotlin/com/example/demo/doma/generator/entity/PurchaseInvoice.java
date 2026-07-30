package com.example.demo.doma.generator.entity;

import com.example.demo.data.domain.PurchaseInvoiceType;
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
@Entity(listener = PurchaseInvoiceListener.class, metamodel = @Metamodel)
@Table(name = "purchase_invoice")
public class PurchaseInvoice extends AbstractPurchaseInvoice {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 仕入伝票種別 */
    @Column(name = "purchase_invoice_type")
    PurchaseInvoiceType purchaseInvoiceType;

    /** 返品元仕入伝票ID */
    @Column(name = "return_purchase_invoice_id")
    Long returnPurchaseInvoiceId;

    /** 仕入伝票日付 */
    @Column(name = "purchase_invoice_date")
    LocalDate purchaseInvoiceDate;

    /** 仕入先ID */
    @Column(name = "supplier_id")
    Long supplierId;

    /** 入庫店舗ID */
    @Column(name = "receiving_store_id")
    Long receivingStoreId;

    /** 仕入伝票金額 */
    @Column(name = "purchase_invoice_amount")
    Long purchaseInvoiceAmount;

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
     * Returns the purchaseInvoiceType.
     * 
     * @return the purchaseInvoiceType
     */
    public PurchaseInvoiceType getPurchaseInvoiceType() {
        return purchaseInvoiceType;
    }

    /** 
     * Sets the purchaseInvoiceType.
     * 
     * @param purchaseInvoiceType the purchaseInvoiceType
     */
    public void setPurchaseInvoiceType(PurchaseInvoiceType purchaseInvoiceType) {
        this.purchaseInvoiceType = purchaseInvoiceType;
    }

    /** 
     * Returns the returnPurchaseInvoiceId.
     * 
     * @return the returnPurchaseInvoiceId
     */
    public Long getReturnPurchaseInvoiceId() {
        return returnPurchaseInvoiceId;
    }

    /** 
     * Sets the returnPurchaseInvoiceId.
     * 
     * @param returnPurchaseInvoiceId the returnPurchaseInvoiceId
     */
    public void setReturnPurchaseInvoiceId(Long returnPurchaseInvoiceId) {
        this.returnPurchaseInvoiceId = returnPurchaseInvoiceId;
    }

    /** 
     * Returns the purchaseInvoiceDate.
     * 
     * @return the purchaseInvoiceDate
     */
    public LocalDate getPurchaseInvoiceDate() {
        return purchaseInvoiceDate;
    }

    /** 
     * Sets the purchaseInvoiceDate.
     * 
     * @param purchaseInvoiceDate the purchaseInvoiceDate
     */
    public void setPurchaseInvoiceDate(LocalDate purchaseInvoiceDate) {
        this.purchaseInvoiceDate = purchaseInvoiceDate;
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
     * Returns the purchaseInvoiceAmount.
     * 
     * @return the purchaseInvoiceAmount
     */
    public Long getPurchaseInvoiceAmount() {
        return purchaseInvoiceAmount;
    }

    /** 
     * Sets the purchaseInvoiceAmount.
     * 
     * @param purchaseInvoiceAmount the purchaseInvoiceAmount
     */
    public void setPurchaseInvoiceAmount(Long purchaseInvoiceAmount) {
        this.purchaseInvoiceAmount = purchaseInvoiceAmount;
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
