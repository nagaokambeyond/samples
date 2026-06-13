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
@Entity(listener = BookStockListener.class, metamodel = @Metamodel)
@Table(name = "book_stock")
public class BookStock extends AbstractBookStock {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /** 本在庫店舗ID */
    @Column(name = "book_stock_store_id")
    Long bookStockStoreId;

    /** 本在庫本ID */
    @Column(name = "book_stock_book_id")
    Long bookStockBookId;

    /** 本在庫数量 */
    @Column(name = "book_stock_quantity")
    Integer bookStockQuantity;

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
     * Returns the bookStockStoreId.
     * 
     * @return the bookStockStoreId
     */
    public Long getBookStockStoreId() {
        return bookStockStoreId;
    }

    /** 
     * Sets the bookStockStoreId.
     * 
     * @param bookStockStoreId the bookStockStoreId
     */
    public void setBookStockStoreId(Long bookStockStoreId) {
        this.bookStockStoreId = bookStockStoreId;
    }

    /** 
     * Returns the bookStockBookId.
     * 
     * @return the bookStockBookId
     */
    public Long getBookStockBookId() {
        return bookStockBookId;
    }

    /** 
     * Sets the bookStockBookId.
     * 
     * @param bookStockBookId the bookStockBookId
     */
    public void setBookStockBookId(Long bookStockBookId) {
        this.bookStockBookId = bookStockBookId;
    }

    /** 
     * Returns the bookStockQuantity.
     * 
     * @return the bookStockQuantity
     */
    public Integer getBookStockQuantity() {
        return bookStockQuantity;
    }

    /** 
     * Sets the bookStockQuantity.
     * 
     * @param bookStockQuantity the bookStockQuantity
     */
    public void setBookStockQuantity(Integer bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
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
