package com.example.demo.doma.generator.entity;

import com.example.demo.data.domain.BookStockMovementSourceType;
import com.example.demo.data.domain.BookStockMovementType;
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
@Entity(listener = BookStockMovementListener.class, metamodel = @Metamodel)
@Table(name = "book_stock_movement")
public class BookStockMovement extends AbstractBookStockMovement {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 店舗ID */
    @Column(name = "store_id")
    Long storeId;

    /** 本ID */
    @Column(name = "book_id")
    Long bookId;

    /** 在庫増減種別 */
    @Column(name = "movement_type")
    BookStockMovementType movementType;

    /** 増減数量 */
    @Column(name = "quantity_delta")
    Integer quantityDelta;

    /** 発生元種別 */
    @Column(name = "source_type")
    BookStockMovementSourceType sourceType;

    /** 発生元ID */
    @Column(name = "source_id")
    Long sourceId;

    /** 発生元明細ID */
    @Column(name = "source_detail_id")
    Long sourceDetailId;

    /** 在庫増減日付 */
    @Column(name = "movement_date")
    LocalDate movementDate;

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
     * Returns the storeId.
     * 
     * @return the storeId
     */
    public Long getStoreId() {
        return storeId;
    }

    /** 
     * Sets the storeId.
     * 
     * @param storeId the storeId
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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
     * Returns the movementType.
     * 
     * @return the movementType
     */
    public BookStockMovementType getMovementType() {
        return movementType;
    }

    /** 
     * Sets the movementType.
     * 
     * @param movementType the movementType
     */
    public void setMovementType(BookStockMovementType movementType) {
        this.movementType = movementType;
    }

    /** 
     * Returns the quantityDelta.
     * 
     * @return the quantityDelta
     */
    public Integer getQuantityDelta() {
        return quantityDelta;
    }

    /** 
     * Sets the quantityDelta.
     * 
     * @param quantityDelta the quantityDelta
     */
    public void setQuantityDelta(Integer quantityDelta) {
        this.quantityDelta = quantityDelta;
    }

    /** 
     * Returns the sourceType.
     * 
     * @return the sourceType
     */
    public BookStockMovementSourceType getSourceType() {
        return sourceType;
    }

    /** 
     * Sets the sourceType.
     * 
     * @param sourceType the sourceType
     */
    public void setSourceType(BookStockMovementSourceType sourceType) {
        this.sourceType = sourceType;
    }

    /** 
     * Returns the sourceId.
     * 
     * @return the sourceId
     */
    public Long getSourceId() {
        return sourceId;
    }

    /** 
     * Sets the sourceId.
     * 
     * @param sourceId the sourceId
     */
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    /** 
     * Returns the sourceDetailId.
     * 
     * @return the sourceDetailId
     */
    public Long getSourceDetailId() {
        return sourceDetailId;
    }

    /** 
     * Sets the sourceDetailId.
     * 
     * @param sourceDetailId the sourceDetailId
     */
    public void setSourceDetailId(Long sourceDetailId) {
        this.sourceDetailId = sourceDetailId;
    }

    /** 
     * Returns the movementDate.
     * 
     * @return the movementDate
     */
    public LocalDate getMovementDate() {
        return movementDate;
    }

    /** 
     * Sets the movementDate.
     * 
     * @param movementDate the movementDate
     */
    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
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
