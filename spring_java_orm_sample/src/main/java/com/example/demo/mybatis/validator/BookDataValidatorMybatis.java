package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.mapper.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookDataValidatorMybatis {
    private final PublisherMapper publisherMapper;

    public void foreignKeyValidate(Long publisherId) {
        final var publisher = publisherMapper.selectByPrimaryKey(publisherId);
        if (Objects.isNull(publisher)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }

    public void versionValidate(BookEntity book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookEntity.class, book.getId());
        }
    }
}
