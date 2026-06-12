package com.example.demo.jpa.service;

import com.example.demo.api.response.BookPageResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.converter.BookConverter;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.jpa.validator.BookDataValidatorJPA;
import com.example.demo.service.BookService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookServiceJPA implements BookService {
    private final BookRepository bookRepository;
    private final BookConverter converter;
    private final BookDataValidatorJPA dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return bookRepository.findByIdWithPublisherName(id)
            .map(converter::toResponse)
            .orElseThrow(RepositoryDataNotfoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var books = bookRepository.findByTitleContainingIgnoreCase(keyword, releaseDateFrom, releaseDateTo, PageRequest.of(page, size));
        return new BookPageResponse(
            converter.toResponse(books.getContent()),
            page,
            size,
            books.getTotalElements(),
            books.getTotalPages()
        );
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var book = bookRepository.save(new Book(null, request.getTitle(), request.getAuthor(), request.getReleaseDate(), request.getPublisherId(), request.getGenreId(), null, null, 1L));
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        return bookRepository.findByIdWithWriteLock(request.getId())
            .map(b -> {
                dataValidator.versionValidate(b, request.getVersion());
                b.setTitle(request.getTitle());
                b.setAuthor(request.getAuthor());
                b.setReleaseDate(request.getReleaseDate());
                b.setPublisherId(request.getPublisherId());
                b.setGenreId(request.getGenreId());
                final var book = bookRepository.save(b);
                return findById(book.getId());
            })
            .orElseThrow(RepositoryDataNotfoundException::new);
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        Book book = bookRepository.findByIdWithWriteLock(id)
            .orElseThrow(RepositoryDataNotfoundException::new);

        bookRepository.deleteById(book.getId());
    }
}
