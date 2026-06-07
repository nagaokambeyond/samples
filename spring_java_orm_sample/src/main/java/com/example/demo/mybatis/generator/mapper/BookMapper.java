package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.894121+09:00", comments="Source Table: book")
    long countByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.894806+09:00", comments="Source Table: book")
    int deleteByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.894972+09:00", comments="Source Table: book")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895126+09:00", comments="Source Table: book")
    int insert(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895251+09:00", comments="Source Table: book")
    int insertSelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895496+09:00", comments="Source Table: book")
    List<BookEntity> selectByExample(BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895628+09:00", comments="Source Table: book")
    BookEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895753+09:00", comments="Source Table: book")
    int updateByExampleSelective(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.895966+09:00", comments="Source Table: book")
    int updateByExample(@Param("row") BookEntity row, @Param("example") BookEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.896254+09:00", comments="Source Table: book")
    int updateByPrimaryKeySelective(BookEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-07T15:53:40.896492+09:00", comments="Source Table: book")
    int updateByPrimaryKey(BookEntity row);
}