package com.example.demo.doma.dao

import com.example.demo.doma.generator.entity.BookStock
import org.seasar.doma.Dao
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable

@Dao
@ConfigAutowireable
interface BookStockCustomDao {
    @Select
    fun selectByStoreIdAndBookIdWithWriteLock(bookStockStoreId: Long?, bookStockBookId: Long?): BookStock?
}
