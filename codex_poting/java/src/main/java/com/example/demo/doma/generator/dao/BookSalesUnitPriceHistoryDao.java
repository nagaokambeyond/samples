package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.BookSalesUnitPriceHistory;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

/**
 */
@Dao
@ConfigAutowireable
public interface BookSalesUnitPriceHistoryDao {

    /**
     * @param id
     * @return the BookSalesUnitPriceHistory entity
     */
    @Select
    BookSalesUnitPriceHistory selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the BookSalesUnitPriceHistory entity
     */
    @Select(ensureResult = true)
    BookSalesUnitPriceHistory selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(BookSalesUnitPriceHistory entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(BookSalesUnitPriceHistory entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(BookSalesUnitPriceHistory entity);
}
