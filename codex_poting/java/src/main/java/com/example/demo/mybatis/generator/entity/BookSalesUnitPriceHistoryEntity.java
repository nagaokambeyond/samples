package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本販売単価履歴
 */
public class BookSalesUnitPriceHistoryEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   本ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.book_id")
    private Long bookId;

    /**
     * Database Column Remarks:
     *   販売単価
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.sales_unit_price")
    private Integer salesUnitPrice;

    /**
     * Database Column Remarks:
     *   有効開始日
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_from")
    private LocalDate effectiveFrom;

    /**
     * Database Column Remarks:
     *   有効終了日
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_to")
    private LocalDate effectiveTo;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.book_id")
    public Long getBookId() {
        return bookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.book_id")
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.sales_unit_price")
    public Integer getSalesUnitPrice() {
        return salesUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.sales_unit_price")
    public void setSalesUnitPrice(Integer salesUnitPrice) {
        this.salesUnitPrice = salesUnitPrice;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_from")
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_from")
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_to")
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.effective_to")
    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: book_sales_unit_price_history.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}