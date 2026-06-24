package com.example.demo.mybatis.converter;

import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.demo.mybatis.entity.BookWithPublisherName;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookOperationConverterMybatis {
    private final ModelMapper modelMapper;

    public BookResponse toResponse(BookWithPublisherName book) {
        final var response = modelMapper.map(book, BookResponse.class);
        response.setBookStockList(book.getBookStockList().stream()
            .map(bookStock -> modelMapper.map(bookStock, BookStockResponse.class))
            .toList());
        return response;
    }

    public List<BookResponse> toResponse(List<BookWithPublisherName> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
