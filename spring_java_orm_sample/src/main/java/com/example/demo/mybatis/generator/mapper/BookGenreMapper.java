package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import com.example.demo.mybatis.generator.entity.BookGenreEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookGenreMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    long countByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int deleteByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int insert(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int insertSelective(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    List<BookGenreEntity> selectByExample(BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    BookGenreEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int updateByExampleSelective(@Param("row") BookGenreEntity row, @Param("example") BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int updateByExample(@Param("row") BookGenreEntity row, @Param("example") BookGenreEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int updateByPrimaryKeySelective(BookGenreEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_genre")
    int updateByPrimaryKey(BookGenreEntity row);
}