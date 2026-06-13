package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.PublisherEntity;
import com.example.demo.mybatis.generator.entity.PublisherEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PublisherMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    long countByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int deleteByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int insert(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int insertSelective(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    List<PublisherEntity> selectByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    PublisherEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int updateByExampleSelective(@Param("row") PublisherEntity row, @Param("example") PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int updateByExample(@Param("row") PublisherEntity row, @Param("example") PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int updateByPrimaryKeySelective(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: publisher")
    int updateByPrimaryKey(PublisherEntity row);
}