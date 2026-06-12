package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import com.example.demo.mybatis.generator.entity.BookGenreEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookGenreMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742622+09:00", comments="Source Table: book_genre")
    long countByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742648+09:00", comments="Source Table: book_genre")
    int deleteByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742666+09:00", comments="Source Table: book_genre")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742682+09:00", comments="Source Table: book_genre")
    int insert(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742698+09:00", comments="Source Table: book_genre")
    int insertSelective(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742716+09:00", comments="Source Table: book_genre")
    List<BookGenreEntity> selectByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742735+09:00", comments="Source Table: book_genre")
    BookGenreEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742753+09:00", comments="Source Table: book_genre")
    int updateByExampleSelective(@Param("row") BookGenreEntity row, @Param("example") BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742774+09:00", comments="Source Table: book_genre")
    int updateByExample(@Param("row") BookGenreEntity row, @Param("example") BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742798+09:00", comments="Source Table: book_genre")
    int updateByPrimaryKeySelective(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-06-12T21:00:55.742821+09:00", comments="Source Table: book_genre")
    int updateByPrimaryKey(BookGenreEntity row);
}