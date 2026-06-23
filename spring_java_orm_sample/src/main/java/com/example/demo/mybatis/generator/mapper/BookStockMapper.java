package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookStockEntity;
import com.example.demo.mybatis.generator.entity.BookStockEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookStockMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    long countByExample(BookStockEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int deleteByExample(BookStockEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int insert(BookStockEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int insertSelective(BookStockEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    List<BookStockEntity> selectByExample(BookStockEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    BookStockEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int updateByExampleSelective(@Param("row") BookStockEntity row, @Param("example") BookStockEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int updateByExample(@Param("row") BookStockEntity row, @Param("example") BookStockEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int updateByPrimaryKeySelective(BookStockEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    int updateByPrimaryKey(BookStockEntity row);
}