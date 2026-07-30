package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

/**
 * Database Table Remarks:
 *   仕入先
 */
public class SupplierEntity {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.id")
    private Long id;

    /**
     * Database Column Remarks:
     *   仕入先名
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.supplier_name")
    private String supplierName;

    /**
     * Database Column Remarks:
     *   作成日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.create_at")
    private LocalDateTime createAt;

    /**
     * Database Column Remarks:
     *   更新日時
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.update_at")
    private LocalDateTime updateAt;

    /**
     * Database Column Remarks:
     *   バージョン
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.supplier_name")
    public String getSupplierName() {
        return supplierName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.supplier_name")
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.create_at")
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.create_at")
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.update_at")
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.update_at")
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: supplier.version")
    public void setVersion(Long version) {
        this.version = version;
    }
}