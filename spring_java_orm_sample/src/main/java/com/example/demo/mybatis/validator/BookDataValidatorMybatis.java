package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
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
        final var bookGenre = bookGenreMapper.selectByPrimaryKey(genreId);
        if (Objects.isNull(publisher) || Objects.isNull(bookGenre)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }

    public void versionValidate(BookEntity book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookEntity.class, book.getId());
        }
    }
}
