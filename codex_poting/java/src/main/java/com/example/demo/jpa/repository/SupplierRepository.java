package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
