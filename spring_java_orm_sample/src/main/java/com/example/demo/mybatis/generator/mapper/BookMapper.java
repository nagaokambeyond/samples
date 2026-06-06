package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.981616+09:00", comments="Source Table: book")
    long countByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.982753+09:00", comments="Source Table: book")
    int deleteByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.982997+09:00", comments="Source Table: book")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.983155+09:00", comments="Source Table: book")
    int insert(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.983308+09:00", comments="Source Table: book")
    int insertSelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.983574+09:00", comments="Source Table: book")
    List<BookEntity> selectByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.983726+09:00", comments="Source Table: book")
    BookEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.983877+09:00", comments="Source Table: book")
    int updateByExampleSelective(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.984109+09:00", comments="Source Table: book")
    int updateByExample(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.984495+09:00", comments="Source Table: book")
    int updateByPrimaryKeySelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T00:19:50.985209+09:00", comments="Source Table: book")
    int updateByPrimaryKey(BookEntity row);
}