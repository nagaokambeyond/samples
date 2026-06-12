package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   出版社
 */
public class PublisherEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741146+09:00", comments="Source field: publisher.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   出版社名
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.74119+09:00", comments="Source field: publisher.publisher_name")
    private String publisherName;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741225+09:00", comments="Source field: publisher.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741259+09:00", comments="Source field: publisher.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.74129+09:00", comments="Source field: publisher.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741163+09:00", comments="Source field: publisher.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741181+09:00", comments="Source field: publisher.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741203+09:00", comments="Source field: publisher.publisher_name")
    public String getPublisherName() {
        return publisherName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741216+09:00", comments="Source field: publisher.publisher_name")
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741238+09:00", comments="Source field: publisher.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741249+09:00", comments="Source field: publisher.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741271+09:00", comments="Source field: publisher.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741281+09:00", comments="Source field: publisher.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741301+09:00", comments="Source field: publisher.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741311+09:00", comments="Source field: publisher.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}