package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.PurchaseOrderEntity;
import com.example.demo.mybatis.generator.entity.PurchaseOrderEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PurchaseOrderMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    long countByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int deleteByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int insert(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int insertSelective(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    List<PurchaseOrderEntity> selectByExample(PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    PurchaseOrderEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int updateByExampleSelective(@Param("row") PurchaseOrderEntity row, @Param("example") PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int updateByExample(@Param("row") PurchaseOrderEntity row, @Param("example") PurchaseOrderEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int updateByPrimaryKeySelective(PurchaseOrderEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    int updateByPrimaryKey(PurchaseOrderEntity row);
}