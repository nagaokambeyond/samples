package com.example.demo.mybatis.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.converter.BookConverter;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.mapper.BookCustomMapper;
import com.example.demo.mybatis.validator.BookDataValidatorMybatis;
import com.example.demo.service.BookService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceMybatis implements BookService {
    private final BookMapper bookMapper;
    private final BookCustomMapper bookCustomMapper;
    private final BookConverter converter;
    private final BookDataValidatorMybatis dataValidator;

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> findAll() {
        final var example = new BookEntityExample();
        example.setOrderByClause("id");
        return converter.toResponseFromBookEntities(bookMapper.selectByExample(example));
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return converter.toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> search(@NonNull String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        return converter.toResponseFromBookEntities(bookCustomMapper.selectByTitleContainingIgnoreCase(keyword, releaseDateFrom, releaseDateTo));
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        final var now = LocalDateTime.now();
        final var book = new BookEntity();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setCreateAt(now);
        book.setUpdateAt(now);
        book.setVersion(1L);

        bookCustomMapper.insertWithGeneratedKey(book);
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(request.getId());
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        BookVersionValidator.validate(book, request.getVersion());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setUpdateAt(LocalDateTime.now());
        book.setVersion(book.getVersion() + 1);

        bookMapper.updateByPrimaryKey(book);
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        bookMapper.deleteByPrimaryKey(book.getId());
    }

    private BookEntity findEntityById(Long id) {
        final var book = bookMapper.selectByPrimaryKey(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }
}
