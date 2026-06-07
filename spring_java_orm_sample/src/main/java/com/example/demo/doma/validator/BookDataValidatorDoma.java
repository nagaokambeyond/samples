package com.example.demo.doma.validator;

import com.example.demo.doma.generator.dao.PublisherDao;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookDataValidatorDoma {
    private final PublisherDao publisherDao;

    public void foreignKeyValidate(Long publisherId){
        final var publisher = publisherDao.selectById(publisherId);
        if (Objects.isNull(publisher)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }
}
