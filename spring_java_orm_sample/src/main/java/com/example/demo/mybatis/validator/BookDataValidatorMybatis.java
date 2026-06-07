package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.mapper.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookDataValidatorMybatis {
    private final PublisherMapper publisherMapper;

    public void foreignKeyValidate(Long publisherId) {
        final var publisher = publisherMapper.selectByPrimaryKey(publisherId);
        if (publisher == null) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }
}
