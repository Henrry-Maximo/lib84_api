package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.BookRecordDto;
import com.example.apirestspringboot.entity.Book;
import com.example.apirestspringboot.exception.BookNotFoundException;
import com.example.apirestspringboot.repository.AuthorRepository;
import com.example.apirestspringboot.repository.BookRepository;
import com.example.apirestspringboot.repository.CategoryRepository;
import com.example.apirestspringboot.repository.SupplierRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
                       SupplierRepository supplierRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.authorRepository = authorRepository;
    }

    public Book create(BookRecordDto dto) {
        var book = new Book();
        BeanUtils.copyProperties(dto, book, "categoryId", "supplierId", "authorIds");
        book.setCategory(categoryRepository.findById(dto.categoryId()).orElseThrow());
        book.setSupplier(supplierRepository.findById(dto.supplierId()).orElseThrow());
        if (dto.authorIds() != null) {
            book.setAuthors(new HashSet<>(authorRepository.findAllById(dto.authorIds())));
        }
        return this.bookRepository.save(book);
    }

    public Page<Book> getAll(Pageable pageable) {
        return this.bookRepository.findAll(pageable);
    }

    public Book getById(UUID id) {
        return this.bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book update(Book book, BookRecordDto dto) {
        BeanUtils.copyProperties(dto, book, "categoryId", "supplierId", "authorIds");
        book.setCategory(categoryRepository.findById(dto.categoryId()).orElseThrow());
        book.setSupplier(supplierRepository.findById(dto.supplierId()).orElseThrow());
        if (dto.authorIds() != null) {
            book.setAuthors(new HashSet<>(authorRepository.findAllById(dto.authorIds())));
        }
        return this.bookRepository.save(book);
    }

    public List<Book> getByCategory(UUID categoryId) {
        return this.bookRepository.findByCategoryId(categoryId);
    }

    public void delete(Book book) {
        this.bookRepository.deleteById(book.getId());
    }
}
