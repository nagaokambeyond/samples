package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.SupplierEntity;
import com.example.demo.mybatis.generator.entity.SupplierEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SupplierMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    long countByExample(SupplierEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int deleteByExample(SupplierEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int insert(SupplierEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int insertSelective(SupplierEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    List<SupplierEntity> selectByExample(SupplierEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    SupplierEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int updateByExampleSelective(@Param("row") SupplierEntity row, @Param("example") SupplierEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int updateByExample(@Param("row") SupplierEntity row, @Param("example") SupplierEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int updateByPrimaryKeySelective(SupplierEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: supplier")
    int updateByPrimaryKey(SupplierEntity row);
}