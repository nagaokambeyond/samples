package com.example.demo.mybatis.mapper;

import com.example.demo.mybatis.entity.BookWithPublisherName;
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntity;
import com.example.demo.mybatis.generator.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BookCustomMapper {
    BookWithPublisherName selectByPrimaryKeyWithPublisherName(@Param("id") Long id);

    List<BookWithPublisherName> selectByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo,
        @Param("limit") int limit,
        @Param("offset") long offset
    );

    long countByTitleOrAuthorStartingWithIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo
    );

    BookEntity selectByPrimaryKeyWithWriteLock(@Param("id") Long id);

    void insertWithGeneratedKey(BookEntity row);

    Long selectNextSalesUnitPriceHistoryId();

    void insertSalesUnitPriceHistoryWithId(BookSalesUnitPriceHistoryEntity row);
}
