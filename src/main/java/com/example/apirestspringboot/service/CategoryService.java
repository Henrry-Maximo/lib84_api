package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.CategoryRecordDto;
import com.example.apirestspringboot.entity.Category;
import com.example.apirestspringboot.exception.CategoryByNameNotFoundException;
import com.example.apirestspringboot.exception.CategoryNotFoundException;
import com.example.apirestspringboot.exception.DuplicateCategoryTitleException;
import com.example.apirestspringboot.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category create(CategoryRecordDto dto) {
        var titleAlreadyExists = categoryRepository.existsByTitleIgnoreCase(dto.title());

        if (titleAlreadyExists) {
            throw new DuplicateCategoryTitleException(dto.title());
        }

        var category = new Category();

        category.setTitle(dto.title());
        category.setDescription(dto.description());

        return this.categoryRepository.save(category);
    }

    public Page<Category> getAll(Pageable pageable) {
        return this.categoryRepository.findAll(pageable);
    }

    public Category getById(UUID id) {
        var category = this.categoryRepository.findById(id);

        if (category.isEmpty()) {
            throw new CategoryNotFoundException(id);
        }

        return category.get();
    }

    public Category getByTitle(String title) {
        var category = this.categoryRepository.findByTitleContainingIgnoreCase(title);

        if (category.isEmpty()) {
            throw new CategoryByNameNotFoundException(title);
        }

        return category.get();
    }

    @Transactional
    public Category update(UUID id, CategoryRecordDto dto) {
        var category = this.getById(id);

        var titleAlreadyExists = !category.getTitle().equalsIgnoreCase(dto.title())
                && categoryRepository.existsByTitleIgnoreCase(dto.title());

        if (titleAlreadyExists) {
            throw new DuplicateCategoryTitleException(dto.title());
        }

        category.setTitle(dto.title());
        category.setDescription(dto.description());

        return this.categoryRepository.save(category);
    }

    @Transactional
    public void delete(UUID id) {
        var category = this.getById(id);

        this.categoryRepository.delete(category);
    }
}
