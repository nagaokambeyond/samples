package com.example.demo.jpa.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceJPA implements BookService {
    private final BookRepository bookRepository;
    private final BookConverter converter;
    private final BookDataValidatorJPA dataValidator;

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> findAll() {
        return converter.toResponse(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return bookRepository.findById(id)
            .map(converter::toResponse)
            .orElseThrow(RepositoryDataNotfoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> search(@NonNull String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        return converter.toResponse(
            bookRepository.findByTitleContainingIgnoreCase(keyword, releaseDateFrom, releaseDateTo)
        );
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        return converter.toResponse(
            bookRepository.save(new Book(null, request.getTitle(), request.getAuthor(), request.getReleaseDate(), request.getPublisherId(), null, null, 1L))
        );
    }

    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        return bookRepository.findByIdWithWriteLock(request.getId())
            .map(b -> {
                dataValidator.versionValidate(b, request.getVersion());
                b.setTitle(request.getTitle());
                b.setAuthor(request.getAuthor());
                b.setReleaseDate(request.getReleaseDate());
                b.setPublisherId(request.getPublisherId());
                return converter.toResponse(bookRepository.save(b));
            })
            .orElseThrow(RepositoryDataNotfoundException::new);
    }

    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        Book book = bookRepository.findByIdWithWriteLock(id)
            .orElseThrow(RepositoryDataNotfoundException::new);

        bookRepository.deleteById(book.getId());
    }
}
