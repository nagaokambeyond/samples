package com.example.demo.doma.generator.entity;

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
@Entity(listener = BookSalesUnitPriceHistoryListener.class, metamodel = @Metamodel)
@Table(name = "book_sales_unit_price_history")
public class BookSalesUnitPriceHistory extends AbstractBookSalesUnitPriceHistory {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 本ID */
    @Column(name = "book_id")
    Long bookId;

    /** 販売単価 */
    @Column(name = "sales_unit_price")
    Integer salesUnitPrice;

    /** 有効開始日 */
    @Column(name = "effective_from")
    LocalDate effectiveFrom;

    /** 有効終了日 */
    @Column(name = "effective_to")
    LocalDate effectiveTo;

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
     * Returns the bookId.
     * 
     * @return the bookId
     */
    public Long getBookId() {
        return bookId;
    }

    /** 
     * Sets the bookId.
     * 
     * @param bookId the bookId
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /** 
     * Returns the salesUnitPrice.
     * 
     * @return the salesUnitPrice
     */
    public Integer getSalesUnitPrice() {
        return salesUnitPrice;
    }

    /** 
     * Sets the salesUnitPrice.
     * 
     * @param salesUnitPrice the salesUnitPrice
     */
    public void setSalesUnitPrice(Integer salesUnitPrice) {
        this.salesUnitPrice = salesUnitPrice;
    }

    /** 
     * Returns the effectiveFrom.
     * 
     * @return the effectiveFrom
     */
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    /** 
     * Sets the effectiveFrom.
     * 
     * @param effectiveFrom the effectiveFrom
     */
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /** 
     * Returns the effectiveTo.
     * 
     * @return the effectiveTo
     */
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    /** 
     * Sets the effectiveTo.
     * 
     * @param effectiveTo the effectiveTo
     */
    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
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
