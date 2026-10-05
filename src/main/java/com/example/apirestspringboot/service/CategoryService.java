package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.CategoryRecordDto;
import com.example.apirestspringboot.entity.Category;
import com.example.apirestspringboot.exception.CategoryNotFoundException;
import com.example.apirestspringboot.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(CategoryRecordDto dto) {
        var category = new Category();
        BeanUtils.copyProperties(dto, category);
        return this.categoryRepository.save(category);
    }

    public Page<Category> getAll(Pageable pageable) {
        return this.categoryRepository.findAll(pageable);
    }

    public Category getById(UUID id) {
        return this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category update(Category category, CategoryRecordDto dto) {
        BeanUtils.copyProperties(dto, category);
        return this.categoryRepository.save(category);
    }

    public void delete(Category category) {
        this.categoryRepository.deleteById(category.getId());
    }
}
