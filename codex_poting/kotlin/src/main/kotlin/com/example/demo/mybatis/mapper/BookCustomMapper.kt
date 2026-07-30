package com.example.demo.mybatis.mapper

import com.example.demo.mybatis.entity.BookWithPublisherName
import com.example.demo.mybatis.generator.entity.BookEntity
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDate

@Mapper
interface BookCustomMapper {
    fun selectByPrimaryKeyWithPublisherName(@Param("id") id: Long?): BookWithPublisherName?

    fun selectByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") keyword: String?,
        @Param("releaseDateFrom") releaseDateFrom: LocalDate?,
        @Param("releaseDateTo") releaseDateTo: LocalDate?,
        @Param("limit") limit: Int,
        @Param("offset") offset: Long
    ): MutableList<BookWithPublisherName?>?

    fun countByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") keyword: String?,
        @Param("releaseDateFrom") releaseDateFrom: LocalDate?,
        @Param("releaseDateTo") releaseDateTo: LocalDate?
    ): Long

    fun selectByPrimaryKeyWithWriteLock(@Param("id") id: Long?): BookEntity?

    fun insertWithGeneratedKey(row: BookEntity?)

    fun selectNextSalesUnitPriceHistoryId(): Long?

    fun insertSalesUnitPriceHistoryWithId(row: BookSalesUnitPriceHistoryEntity?)
}
