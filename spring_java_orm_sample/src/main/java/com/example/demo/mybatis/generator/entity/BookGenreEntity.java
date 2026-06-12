package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   本ジャンル
 */
public class BookGenreEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742414+09:00", comments="Source field: book_genre.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   ジャンル名
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742457+09:00", comments="Source field: book_genre.genre_name")
    private String genreName;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742493+09:00", comments="Source field: book_genre.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742525+09:00", comments="Source field: book_genre.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742562+09:00", comments="Source field: book_genre.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742433+09:00", comments="Source field: book_genre.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742447+09:00", comments="Source field: book_genre.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.74247+09:00", comments="Source field: book_genre.genre_name")
    public String getGenreName() {
        return genreName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742484+09:00", comments="Source field: book_genre.genre_name")
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742506+09:00", comments="Source field: book_genre.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742516+09:00", comments="Source field: book_genre.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742542+09:00", comments="Source field: book_genre.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742553+09:00", comments="Source field: book_genre.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742574+09:00", comments="Source field: book_genre.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742584+09:00", comments="Source field: book_genre.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}