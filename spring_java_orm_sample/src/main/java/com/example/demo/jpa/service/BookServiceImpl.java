package com.example.demo.jpa.service;

import com.example.demo.converter.BookConverter;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookConverter converter;

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
    public List<Book> searchByTitle(@NonNull String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        return converter.toResponse(
            bookRepository.save(new Book(null, request.getTitle(), request.getAuthor(), LocalDateTime.now(), LocalDateTime.now(), 1L))
        );
    }

    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        return bookRepository.findByIdWithWriteLock(request.getId())
            .map(b -> {
                BookVersionValidator.validate(b, request.getVersion());
                b.setTitle(request.getTitle());
                b.setAuthor(request.getAuthor());
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
