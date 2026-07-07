package com.example.demo.mybatis.generator.mapper;

import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntity;
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntityExample;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookSalesUnitPriceHistoryMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    long countByExample(BookSalesUnitPriceHistoryEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int deleteByExample(BookSalesUnitPriceHistoryEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int insert(BookSalesUnitPriceHistoryEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int insertSelective(BookSalesUnitPriceHistoryEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    List<BookSalesUnitPriceHistoryEntity> selectByExample(BookSalesUnitPriceHistoryEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    BookSalesUnitPriceHistoryEntity selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int updateByExampleSelective(@Param("row") BookSalesUnitPriceHistoryEntity row, @Param("example") BookSalesUnitPriceHistoryEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int updateByExample(@Param("row") BookSalesUnitPriceHistoryEntity row, @Param("example") BookSalesUnitPriceHistoryEntityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int updateByPrimaryKeySelective(BookSalesUnitPriceHistoryEntity row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    int updateByPrimaryKey(BookSalesUnitPriceHistoryEntity row);
}