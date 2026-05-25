package com.example.demo.converter;

import com.example.demo.api.response.BookResponse;
import org.springframework.stereotype.Component;
import com.example.demo.jpa.entity.Book;

import java.util.List;


@Component
public class BookConverter {
    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getUpdateAt(), book.getVersion());
    }

    public List<BookResponse> toResponse(List<Book> books) {
        return books.stream().map(book-> new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getUpdateAt(), book.getVersion())).toList();
    }
}
