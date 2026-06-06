package com.example.demo.doma.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookServiceDomaTest {
    @Autowired
    private BookService bookService;

    @Test
    void usesDomaAsPrimaryBookService() {
        assertThat(bookService).isInstanceOf(BookServiceDoma.class);
    }

    @Test
    void findAllReturnsBooksOrderedById() {
        var books = bookService.findAll();

        assertThat(books).hasSizeGreaterThanOrEqualTo(2);
        assertThat(books).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void findByIdReturnsBook() {
        var book = bookService.findById(1L);

        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Spring入門");
    }

    @Test
    void findByIdThrowsWhenBookDoesNotExist() {
        assertThatThrownBy(() -> bookService.findById(999L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void searchByTitleIgnoresCase() {
        var books = bookService.searchByTitle("spring");

        assertThat(books).extracting("title").containsExactly("Spring入門");
    }

    @Test
    void createReturnsGeneratedIdAndResponse() {
        var book = bookService.create(new BookCreateRequest("Doma入門", "Jiro"));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Doma入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getVersion()).isEqualTo(1L);
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        var before = bookService.findById(1L);

        var updated = bookService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", before.getVersion()));

        assertThat(updated.getTitle()).isEqualTo("Doma更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(updated.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void deleteRemovesBook() {
        bookService.delete(1L);

        assertThatThrownBy(() -> bookService.findById(1L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }
}
