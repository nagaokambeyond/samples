package com.example.demo.jpa.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookDataValidatorJPA {
    private final PublisherRepository publisherRepository;

    public void foreignKeyValidate(Long publisherId) {
        if (!publisherRepository.existsById(publisherId)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }
}
