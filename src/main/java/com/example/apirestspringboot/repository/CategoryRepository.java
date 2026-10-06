package com.example.apirestspringboot.repository;

import com.example.apirestspringboot.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByTitleContainingIgnoreCase(String title);
    boolean existsByTitleIgnoreCase(String title);
}
