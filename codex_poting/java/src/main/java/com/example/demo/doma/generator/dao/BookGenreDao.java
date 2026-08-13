package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.BookGenre;
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
public interface BookGenreDao {

    /**
     * @param id
     * @return the BookGenre entity
     */
    @Select
    BookGenre selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the BookGenre entity
     */
    @Select(ensureResult = true)
    BookGenre selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(BookGenre entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(BookGenre entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(BookGenre entity);
}
