package com.example.demo.doma.dao;

import com.example.demo.doma.entity.BookWithPublisherName;
import com.example.demo.doma.entity.BookWithPublisherNameAggregateStrategy;
import com.example.demo.doma.generator.entity.Book;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import java.time.LocalDate;
import java.util.List;

@Dao
@ConfigAutowireable
public interface BookCustomDao {
    @Select(aggregateStrategy = BookWithPublisherNameAggregateStrategy.class)
    List<BookWithPublisherName> selectByTitleOrAuthorStartingWithIgnoreCase(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int limit, long offset);

    @Select
    long countByTitleOrAuthorStartingWithIgnoreCase(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo);

    @Select(aggregateStrategy = BookWithPublisherNameAggregateStrategy.class)
    BookWithPublisherName selectByIdWithPublisherName(Long id);

    @Select
    Book selectByIdWithWriteLock(Long id);
}
