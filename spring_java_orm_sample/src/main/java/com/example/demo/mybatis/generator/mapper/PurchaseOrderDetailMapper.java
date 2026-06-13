package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntity;
import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PurchaseOrderDetailMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    long countByExample(PurchaseOrderDetailEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int deleteByExample(PurchaseOrderDetailEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int insert(PurchaseOrderDetailEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int insertSelective(PurchaseOrderDetailEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    List<PurchaseOrderDetailEntity> selectByExample(PurchaseOrderDetailEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    PurchaseOrderDetailEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int updateByExampleSelective(@Param("row") PurchaseOrderDetailEntity row, @Param("example") PurchaseOrderDetailEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int updateByExample(@Param("row") PurchaseOrderDetailEntity row, @Param("example") PurchaseOrderDetailEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int updateByPrimaryKeySelective(PurchaseOrderDetailEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    int updateByPrimaryKey(PurchaseOrderDetailEntity row);
}