package com.example.demo.doma.dao

import com.example.demo.doma.generator.entity.BookSalesUnitPriceHistory
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import java.time.LocalDate

@Dao
@ConfigAutowireable
interface BookSalesUnitPriceHistoryCustomDao {
    @Select
    fun selectNextId(): Long?

    @Insert(sqlFile = true)
    fun insertWithId(entity: BookSalesUnitPriceHistory?): Int

    @Select
    fun selectFollowingHistories(bookId: Long?, effectiveFrom: LocalDate?): MutableList<BookSalesUnitPriceHistory?>?

    @Select
    fun selectPreviousHistory(bookId: Long?, effectiveFrom: LocalDate?): BookSalesUnitPriceHistory?
}
