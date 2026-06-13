package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.StoreEntity;
import com.example.demo.mybatis.generator.entity.StoreEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoreMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    long countByExample(StoreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int deleteByExample(StoreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int insert(StoreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int insertSelective(StoreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    List<StoreEntity> selectByExample(StoreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    StoreEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int updateByExampleSelective(@Param("row") StoreEntity row, @Param("example") StoreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int updateByExample(@Param("row") StoreEntity row, @Param("example") StoreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int updateByPrimaryKeySelective(StoreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: store")
    int updateByPrimaryKey(StoreEntity row);
}