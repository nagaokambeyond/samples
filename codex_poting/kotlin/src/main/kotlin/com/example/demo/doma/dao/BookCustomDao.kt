package com.example.demo.doma.dao

import com.example.demo.doma.entity.BookWithPublisherName
import com.example.demo.doma.entity.BookWithPublisherNameAggregateStrategy
import com.example.demo.doma.generator.entity.Book
import org.seasar.doma.Dao
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import java.time.LocalDate

@Dao
@ConfigAutowireable
interface BookCustomDao {
    @Select(aggregateStrategy = BookWithPublisherNameAggregateStrategy::class)
    fun selectByTitleOrAuthorStartingWithIgnoreCase(
        keyword: String?,
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?,
        limit: Int,
        offset: Long
    ): MutableList<BookWithPublisherName?>?

    @Select
    fun countByTitleOrAuthorStartingWithIgnoreCase(
        keyword: String?,
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?
    ): Long

    @Select(aggregateStrategy = BookWithPublisherNameAggregateStrategy::class)
    fun selectByIdWithPublisherName(id: Long?): BookWithPublisherName?

    @Select
    fun selectByIdWithWriteLock(id: Long?): Book?

    @Select
    fun selectByIsbn(isbn: String?): Book?
}
