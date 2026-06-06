package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

public class BookEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829154+09:00", comments="Source field: book.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829437+09:00", comments="Source field: book.title")
    private String title;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829547+09:00", comments="Source field: book.author")
    private String author;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829589+09:00", comments="Source field: book.create_at")
    private LocalDateTime createAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829629+09:00", comments="Source field: book.update_at")
    private LocalDateTime updateAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829669+09:00", comments="Source field: book.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829374+09:00", comments="Source field: book.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829419+09:00", comments="Source field: book.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829454+09:00", comments="Source field: book.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829533+09:00", comments="Source field: book.title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829562+09:00", comments="Source field: book.author")
    public String getAuthor() {
        return author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829577+09:00", comments="Source field: book.author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829604+09:00", comments="Source field: book.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829618+09:00", comments="Source field: book.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829643+09:00", comments="Source field: book.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829656+09:00", comments="Source field: book.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829682+09:00", comments="Source field: book.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.829695+09:00", comments="Source field: book.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}