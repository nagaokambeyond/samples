package com.example.demo.mybatis.service;

import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.mybatis.converter.BookOperationConverterMybatis;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.mapper.BookCustomMapper;
import com.example.demo.mybatis.validator.BookDataValidatorMybatis;
import com.example.demo.service.BooksOperationService;
import com.example.demo.service.PageCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Profile("mybatis")
@RequiredArgsConstructor
public class BooksOperationServiceMybatis implements BooksOperationService {
    private final BookMapper bookMapper;
    private final BookCustomMapper bookCustomMapper;
    private final BookOperationConverterMybatis converter;
    private final BookDataValidatorMybatis dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return converter.toResponse(findByIdWithPublisherName(id));
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var offset = PageCalculator.calculateOffset(page, size);
        final var books = bookCustomMapper.selectByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo, size, offset);
        final var totalElements = bookCustomMapper.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo);
        final var response = new BookPageResponse();
        response.setContent(converter.toResponse(books));
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(PageCalculator.calculateTotalPages(totalElements, size));
        return response;
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var now = LocalDateTime.now();
        final var book = new BookEntity();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setCreateAt(now);
        book.setUpdateAt(now);
        book.setVersion(1L);

        bookCustomMapper.insertWithGeneratedKey(book);
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(request.getId());
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        dataValidator.versionValidate(book, request.getVersion());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setUpdateAt(LocalDateTime.now());
        book.setVersion(book.getVersion() + 1);

        bookMapper.updateByPrimaryKey(book);
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        bookMapper.deleteByPrimaryKey(book.getId());
    }

    private com.example.demo.mybatis.entity.BookWithPublisherName findByIdWithPublisherName(Long id) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithPublisherName(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }

}
