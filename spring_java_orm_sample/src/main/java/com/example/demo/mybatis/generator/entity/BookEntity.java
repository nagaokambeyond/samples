package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本
 */
public class BookEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.978958+09:00", comments="Source field: book.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   タイトル
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980034+09:00", comments="Source field: book.title")
    private String title;

    /**
     * Database Column Remarks:
     *   著者
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980262+09:00", comments="Source field: book.author")
    private String author;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980314+09:00", comments="Source field: book.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.98034+09:00", comments="Source field: book.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980365+09:00", comments="Source field: book.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.979961+09:00", comments="Source field: book.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.98002+09:00", comments="Source field: book.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980059+09:00", comments="Source field: book.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980236+09:00", comments="Source field: book.title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980284+09:00", comments="Source field: book.author")
    public String getAuthor() {
        return author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980306+09:00", comments="Source field: book.author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980325+09:00", comments="Source field: book.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980334+09:00", comments="Source field: book.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.98035+09:00", comments="Source field: book.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980358+09:00", comments="Source field: book.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980373+09:00", comments="Source field: book.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.980381+09:00", comments="Source field: book.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}