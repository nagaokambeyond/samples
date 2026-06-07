package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本
 */
public class BookEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892298+09:00", comments="Source field: book.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   タイトル
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892546+09:00", comments="Source field: book.title")
    private String title;

    /**
     * Database Column Remarks:
     *   著者
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892751+09:00", comments="Source field: book.author")
    private String author;

    /**
     * Database Column Remarks:
     *   発売日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892798+09:00", comments="Source field: book.release_date")
    private LocalDate releaseDate;

    /**
     * Database Column Remarks:
     *   出版社ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892821+09:00", comments="Source field: book.publisher_id")
    private Long publisherId;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892845+09:00", comments="Source field: book.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892866+09:00", comments="Source field: book.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892888+09:00", comments="Source field: book.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892504+09:00", comments="Source field: book.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892533+09:00", comments="Source field: book.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892563+09:00", comments="Source field: book.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892713+09:00", comments="Source field: book.title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892765+09:00", comments="Source field: book.author")
    public String getAuthor() {
        return author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892791+09:00", comments="Source field: book.author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892807+09:00", comments="Source field: book.release_date")
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892815+09:00", comments="Source field: book.release_date")
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892828+09:00", comments="Source field: book.publisher_id")
    public Long getPublisherId() {
        return publisherId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892839+09:00", comments="Source field: book.publisher_id")
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892853+09:00", comments="Source field: book.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892861+09:00", comments="Source field: book.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892874+09:00", comments="Source field: book.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892881+09:00", comments="Source field: book.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892895+09:00", comments="Source field: book.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.892902+09:00", comments="Source field: book.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}