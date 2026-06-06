package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.Book;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface BookDao {

    /**
     * @param id
     * @return the Book entity
     */
    @Select
    Book selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the Book entity
     */
    @Select(ensureResult = true)
    Book selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(Book entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(Book entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(Book entity);
}
