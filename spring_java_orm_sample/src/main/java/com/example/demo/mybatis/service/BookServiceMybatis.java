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
import com.example.demo.service.BookService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class BookServiceMybatis implements BookService {
    private final BookMapper bookMapper;
    private final BookCustomMapper bookCustomMapper;
    private final BookConverter converter;

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
    public List<BookResponse> searchByTitle(@NonNull String keyword) {
        return converter.toResponseFromBookEntities(bookCustomMapper.selectByTitleContainingIgnoreCase(keyword));
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        BookEntity book = new BookEntity();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCreateAt(toDate(now));
        book.setUpdateAt(toDate(now));
        book.setVersion(1L);

        bookCustomMapper.insertWithGeneratedKey(book);
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        BookEntity book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(request.getId());
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }

        BookVersionValidator.validate(book, request.getVersion());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setUpdateAt(toDate(LocalDateTime.now()));
        book.setVersion(book.getVersion() + 1);

        bookMapper.updateByPrimaryKey(book);
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        BookEntity book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(id);
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }

        bookMapper.deleteByPrimaryKey(book.getId());
    }

    private BookEntity findEntityById(Long id) {
        BookEntity book = bookMapper.selectByPrimaryKey(id);
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
