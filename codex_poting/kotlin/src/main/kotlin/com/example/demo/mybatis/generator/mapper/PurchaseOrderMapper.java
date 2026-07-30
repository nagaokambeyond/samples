package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.PurchaseOrderEntity;
import com.example.demo.mybatis.generator.entity.PurchaseOrderEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PurchaseOrderMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    long countByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int deleteByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int insert(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int insertSelective(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    List<PurchaseOrderEntity> selectByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    PurchaseOrderEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int updateByExampleSelective(@Param("row") PurchaseOrderEntity row, @Param("example") PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int updateByExample(@Param("row") PurchaseOrderEntity row, @Param("example") PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int updateByPrimaryKeySelective(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    int updateByPrimaryKey(PurchaseOrderEntity row);
}