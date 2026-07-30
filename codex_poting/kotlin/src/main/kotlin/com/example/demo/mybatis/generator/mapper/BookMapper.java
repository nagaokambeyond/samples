package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    long countByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int deleteByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int insert(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int insertSelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    List<BookEntity> selectByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    BookEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int updateByExampleSelective(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int updateByExample(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int updateByPrimaryKeySelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book")
    int updateByPrimaryKey(BookEntity row);
}