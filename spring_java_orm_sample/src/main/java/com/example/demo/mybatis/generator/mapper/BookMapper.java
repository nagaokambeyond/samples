package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.830961+09:00", comments="Source Table: book")
    long countByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.831711+09:00", comments="Source Table: book")
    int deleteByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.831886+09:00", comments="Source Table: book")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832052+09:00", comments="Source Table: book")
    int insert(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832181+09:00", comments="Source Table: book")
    int insertSelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832451+09:00", comments="Source Table: book")
    List<BookEntity> selectByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832596+09:00", comments="Source Table: book")
    BookEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832727+09:00", comments="Source Table: book")
    int updateByExampleSelective(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.832974+09:00", comments="Source Table: book")
    int updateByExample(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.833285+09:00", comments="Source Table: book")
    int updateByPrimaryKeySelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-06T23:48:11.833561+09:00", comments="Source Table: book")
    int updateByPrimaryKey(BookEntity row);
}