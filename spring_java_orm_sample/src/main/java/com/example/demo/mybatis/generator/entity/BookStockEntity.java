package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本在庫
 */
public class BookStockEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   本在庫店舗ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_store_id")
    private Long bookStockStoreId;

    /**
     * Database Column Remarks:
     *   本在庫本ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_book_id")
    private Long bookStockBookId;

    /**
     * Database Column Remarks:
     *   本在庫数量
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_quantity")
    private Integer bookStockQuantity;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_store_id")
    public Long getBookStockStoreId() {
        return bookStockStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_store_id")
    public void setBookStockStoreId(Long bookStockStoreId) {
        this.bookStockStoreId = bookStockStoreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_book_id")
    public Long getBookStockBookId() {
        return bookStockBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_book_id")
    public void setBookStockBookId(Long bookStockBookId) {
        this.bookStockBookId = bookStockBookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_quantity")
    public Integer getBookStockQuantity() {
        return bookStockQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.book_stock_quantity")
    public void setBookStockQuantity(Integer bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_stock.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}