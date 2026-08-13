package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
