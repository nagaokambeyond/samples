package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本
 */
public class BookEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.248592+09:00", comments="Source field: book.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   タイトル
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.248955+09:00", comments="Source field: book.title")
    private String title;

    /**
     * Database Column Remarks:
     *   著者
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249172+09:00", comments="Source field: book.author")
    private String author;

    /**
     * Database Column Remarks:
     *   発売日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249256+09:00", comments="Source field: book.release_date")
    private LocalDate releaseDate;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249299+09:00", comments="Source field: book.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249334+09:00", comments="Source field: book.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249363+09:00", comments="Source field: book.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.248894+09:00", comments="Source field: book.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.248936+09:00", comments="Source field: book.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249037+09:00", comments="Source field: book.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249152+09:00", comments="Source field: book.title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249187+09:00", comments="Source field: book.author")
    public String getAuthor() {
        return author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249238+09:00", comments="Source field: book.author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249272+09:00", comments="Source field: book.release_date")
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.24929+09:00", comments="Source field: book.release_date")
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.24931+09:00", comments="Source field: book.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.24932+09:00", comments="Source field: book.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249345+09:00", comments="Source field: book.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249355+09:00", comments="Source field: book.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249373+09:00", comments="Source field: book.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T09:31:55.249384+09:00", comments="Source field: book.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}