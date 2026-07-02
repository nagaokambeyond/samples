package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import com.example.demo.mybatis.generator.entity.PublisherEntity;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.generator.mapper.BookGenreMapper;
import com.example.demo.mybatis.generator.mapper.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("mybatis")
@RequiredArgsConstructor
public class BookDataValidatorMybatis {
    private final BookMapper bookMapper;
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

    public void uniqueIsbnValidate(String isbn, Long bookId) {
        final var example = new BookEntityExample();
        example.createCriteria().andIsbnEqualTo(isbn);
        bookMapper.selectByExample(example).stream()
            .filter(book -> !Objects.equals(book.getId(), bookId))
            .findFirst()
            .ifPresent(book -> {
                throw new UniqueConstraintValidationException("book", "isbn", isbn);
            });
    }
}
