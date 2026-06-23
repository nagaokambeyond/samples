package com.example.demo.mybatis.mapper;

import com.example.demo.mybatis.generator.entity.BookStockEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookStockCustomMapper {
    BookStockEntity selectByStoreIdAndBookIdWithWriteLock(
        @Param("bookStockStoreId") Long bookStockStoreId,
        @Param("bookStockBookId") Long bookStockBookId
    );
}
