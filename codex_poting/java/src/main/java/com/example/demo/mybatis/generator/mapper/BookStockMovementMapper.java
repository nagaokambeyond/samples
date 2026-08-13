package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookStockMovementEntity;
import com.example.demo.mybatis.generator.entity.BookStockMovementEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookStockMovementMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    long countByExample(BookStockMovementEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int deleteByExample(BookStockMovementEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int insert(BookStockMovementEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int insertSelective(BookStockMovementEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    List<BookStockMovementEntity> selectByExample(BookStockMovementEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    BookStockMovementEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int updateByExampleSelective(@Param("row") BookStockMovementEntity row, @Param("example") BookStockMovementEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int updateByExample(@Param("row") BookStockMovementEntity row, @Param("example") BookStockMovementEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int updateByPrimaryKeySelective(BookStockMovementEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    int updateByPrimaryKey(BookStockMovementEntity row);
}