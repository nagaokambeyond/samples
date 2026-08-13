package com.example.demo.doma.dao;

import com.example.demo.doma.generator.entity.BookSalesUnitPriceHistory;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import java.time.LocalDate;
import java.util.List;

@Dao
@ConfigAutowireable
public interface BookSalesUnitPriceHistoryCustomDao {
    @Select
    Long selectNextId();

    @Insert(sqlFile = true)
    int insertWithId(BookSalesUnitPriceHistory entity);

    @Select
    List<BookSalesUnitPriceHistory> selectFollowingHistories(Long bookId, LocalDate effectiveFrom);

    @Select
    BookSalesUnitPriceHistory selectPreviousHistory(Long bookId, LocalDate effectiveFrom);
}
