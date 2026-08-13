package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookGenreRepository extends JpaRepository<BookGenre, Long> {
}
