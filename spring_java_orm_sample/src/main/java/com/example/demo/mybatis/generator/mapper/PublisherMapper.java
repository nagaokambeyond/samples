package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.PublisherEntity;
import com.example.demo.mybatis.generator.entity.PublisherEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PublisherMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741351+09:00", comments="Source Table: publisher")
    long countByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741376+09:00", comments="Source Table: publisher")
    int deleteByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741399+09:00", comments="Source Table: publisher")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741416+09:00", comments="Source Table: publisher")
    int insert(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741432+09:00", comments="Source Table: publisher")
    int insertSelective(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741452+09:00", comments="Source Table: publisher")
    List<PublisherEntity> selectByExample(PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741472+09:00", comments="Source Table: publisher")
    PublisherEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741493+09:00", comments="Source Table: publisher")
    int updateByExampleSelective(@Param("row") PublisherEntity row, @Param("example") PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741514+09:00", comments="Source Table: publisher")
    int updateByExample(@Param("row") PublisherEntity row, @Param("example") PublisherEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741541+09:00", comments="Source Table: publisher")
    int updateByPrimaryKeySelective(PublisherEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.741566+09:00", comments="Source Table: publisher")
    int updateByPrimaryKey(PublisherEntity row);
}