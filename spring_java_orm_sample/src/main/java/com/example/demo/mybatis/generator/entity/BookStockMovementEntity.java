package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.BookStockMovementSourceType;
import com.example.demo.data.domain.BookStockMovementType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本在庫増減履歴
 */
public class BookStockMovementEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   店舗ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.store_id")
    private Long storeId;

    /**
     * Database Column Remarks:
     *   本ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.book_id")
    private Long bookId;

    /**
     * Database Column Remarks:
     *   在庫増減種別
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_type")
    private BookStockMovementType movementType;

    /**
     * Database Column Remarks:
     *   増減数量
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.quantity_delta")
    private Integer quantityDelta;

    /**
     * Database Column Remarks:
     *   発生元種別
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_type")
    private BookStockMovementSourceType sourceType;

    /**
     * Database Column Remarks:
     *   発生元ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_id")
    private Long sourceId;

    /**
     * Database Column Remarks:
     *   発生元明細ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_detail_id")
    private Long sourceDetailId;

    /**
     * Database Column Remarks:
     *   在庫増減日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_date")
    private LocalDate movementDate;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.store_id")
    public Long getStoreId() {
        return storeId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.store_id")
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.book_id")
    public Long getBookId() {
        return bookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.book_id")
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_type")
    public BookStockMovementType getMovementType() {
        return movementType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_type")
    public void setMovementType(BookStockMovementType movementType) {
        this.movementType = movementType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.quantity_delta")
    public Integer getQuantityDelta() {
        return quantityDelta;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.quantity_delta")
    public void setQuantityDelta(Integer quantityDelta) {
        this.quantityDelta = quantityDelta;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_type")
    public BookStockMovementSourceType getSourceType() {
        return sourceType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_type")
    public void setSourceType(BookStockMovementSourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_id")
    public Long getSourceId() {
        return sourceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_id")
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_detail_id")
    public Long getSourceDetailId() {
        return sourceDetailId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.source_detail_id")
    public void setSourceDetailId(Long sourceDetailId) {
        this.sourceDetailId = sourceDetailId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_date")
    public LocalDate getMovementDate() {
        return movementDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.movement_date")
    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock_movement.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}