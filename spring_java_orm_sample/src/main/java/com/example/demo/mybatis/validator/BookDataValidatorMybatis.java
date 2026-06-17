package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import com.example.demo.mybatis.generator.entity.PublisherEntity;
import com.example.demo.mybatis.generator.mapper.BookGenreMapper;
import com.example.demo.mybatis.generator.mapper.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookDataValidatorMybatis {
    private final PublisherMapper publisherMapper;
    private final BookGenreMapper bookGenreMapper;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        final var publisher = publisherMapper.selectByPrimaryKey(publisherId);
        if (Objects.isNull(publisher)) {
            throw new ForeignKeyReferenceNotFoundException(PublisherEntity.class, publisherId);
        }

        final var bookGenre = bookGenreMapper.selectByPrimaryKey(genreId);
        if (Objects.isNull(bookGenre)) {
            throw new ForeignKeyReferenceNotFoundException(BookGenreEntity.class, genreId);
        }
    }

    public void versionValidate(BookEntity book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookEntity.class, book.getId());
        }
    }
}
