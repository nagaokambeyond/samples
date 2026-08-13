package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.BookStockMovement;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface BookStockMovementRepository extends JpaRepository<BookStockMovement, Long> {
}
