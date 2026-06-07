package com.example.demo.doma.validator;

import com.example.demo.doma.generator.dao.PublisherDao;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookDataValidatorDoma {
    private final PublisherDao publisherDao;

    public void foreignKeyValidate(Long publisherId){
        final var publisher = publisherDao.selectById(publisherId);
        if (publisher == null) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }
}
