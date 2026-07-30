package com.example.demo.mybatis.mapper

import com.example.demo.mybatis.generator.entity.BookStockEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface BookStockCustomMapper {
    fun selectByStoreIdAndBookIdWithWriteLock(
        @Param("bookStockStoreId") bookStockStoreId: Long?,
        @Param("bookStockBookId") bookStockBookId: Long?
    ): BookStockEntity?
}
