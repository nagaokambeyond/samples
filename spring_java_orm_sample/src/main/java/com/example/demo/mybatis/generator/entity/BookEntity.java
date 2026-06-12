package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本
 */
public class BookEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729277+09:00", comments="Source field: book.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   タイトル
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729605+09:00", comments="Source field: book.title")
    private String title;

    /**
     * Database Column Remarks:
     *   著者
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729777+09:00", comments="Source field: book.author")
    private String author;

    /**
     * Database Column Remarks:
     *   発売日付
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729837+09:00", comments="Source field: book.release_date")
    private LocalDate releaseDate;

    /**
     * Database Column Remarks:
     *   出版社ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729888+09:00", comments="Source field: book.publisher_id")
    private Long publisherId;

    /**
     * Database Column Remarks:
     *   ジャンルID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729991+09:00", comments="Source field: book.genre_id")
    private Long genreId;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.73004+09:00", comments="Source field: book.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730098+09:00", comments="Source field: book.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730151+09:00", comments="Source field: book.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729541+09:00", comments="Source field: book.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729585+09:00", comments="Source field: book.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729654+09:00", comments="Source field: book.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.72976+09:00", comments="Source field: book.title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729801+09:00", comments="Source field: book.author")
    public String getAuthor() {
        return author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729822+09:00", comments="Source field: book.author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729857+09:00", comments="Source field: book.release_date")
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729874+09:00", comments="Source field: book.release_date")
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729905+09:00", comments="Source field: book.publisher_id")
    public Long getPublisherId() {
        return publisherId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.729921+09:00", comments="Source field: book.publisher_id")
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730011+09:00", comments="Source field: book.genre_id")
    public Long getGenreId() {
        return genreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730027+09:00", comments="Source field: book.genre_id")
    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730059+09:00", comments="Source field: book.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730084+09:00", comments="Source field: book.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730117+09:00", comments="Source field: book.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730137+09:00", comments="Source field: book.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730168+09:00", comments="Source field: book.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.730186+09:00", comments="Source field: book.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}