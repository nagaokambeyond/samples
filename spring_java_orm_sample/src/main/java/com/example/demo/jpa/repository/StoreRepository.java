package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
