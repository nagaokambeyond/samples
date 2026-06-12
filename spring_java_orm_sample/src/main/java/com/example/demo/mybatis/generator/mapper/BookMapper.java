package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.731499+09:00", comments="Source Table: book")
    long countByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.732197+09:00", comments="Source Table: book")
    int deleteByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.732356+09:00", comments="Source Table: book")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.732515+09:00", comments="Source Table: book")
    int insert(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.732669+09:00", comments="Source Table: book")
    int insertSelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.73294+09:00", comments="Source Table: book")
    List<BookEntity> selectByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.733095+09:00", comments="Source Table: book")
    BookEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.7333+09:00", comments="Source Table: book")
    int updateByExampleSelective(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.733537+09:00", comments="Source Table: book")
    int updateByExample(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.73394+09:00", comments="Source Table: book")
    int updateByPrimaryKeySelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.734365+09:00", comments="Source Table: book")
    int updateByPrimaryKey(BookEntity row);
}