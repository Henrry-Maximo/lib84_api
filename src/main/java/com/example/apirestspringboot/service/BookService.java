package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.BookRecordDto;
import com.example.apirestspringboot.entity.Book;
import com.example.apirestspringboot.exception.*;
import com.example.apirestspringboot.repository.AuthorRepository;
import com.example.apirestspringboot.repository.BookRepository;
import com.example.apirestspringboot.repository.CategoryRepository;
import com.example.apirestspringboot.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
        var categoryId = dto.categoryId();
        var supplierId = dto.supplierId();
        var authorIds = dto.authorIds();

        var category = this.categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(categoryId);
        }

        var supplier = this.supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            throw new SupplierNotFoundException(supplierId);
        }

        var foundAuthors = this.authorRepository.findAllById(authorIds);
        var uniqueAuthorIds = new HashSet<>(authorIds);

        if (foundAuthors.size() != uniqueAuthorIds.size()) {
            throw new AuthorsNotFoundException();
        }

        var book = new Book();

        book.setTitle(dto.title());
        book.setAmount(dto.amount());
        book.setDatePublication(dto.datePublication());
        book.setIsbn(dto.isbn());
        book.setCategory(category.get());
        book.setSupplier(supplier.get());
        book.setAuthors(new HashSet<>(foundAuthors));

        // BeanUtils.copyProperties(dto, book, "categoryId", "supplierId", "authorIds");
        // book.setCategory(categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new CategoryNotFoundException()));
        // book.setSupplier(supplierRepository.findById(dto.supplierId()).orElseThrow(() -> new CategoryNotFoundException()));

        return this.bookRepository.save(book);
    }

    public Page<Book> getAll(Pageable pageable) {
        return this.bookRepository.findAll(pageable);
    }

    public Book getById(UUID id) {
        Optional<Book> book = this.bookRepository.findById(id);

        if (book.isEmpty()) {
            throw new BookNotFoundException(id);
        }

        return book.get();
    }

    public Book update(Book book, BookRecordDto dto) {
        var categoryId = dto.categoryId();
        var supplierId = dto.supplierId();
        var authorIds = dto.authorIds();

        var category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(categoryId);
        }

        var supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            throw new SupplierNotFoundException(supplierId);
        }

        var foundAuthors = authorRepository.findAllById(authorIds);
        if (foundAuthors.size() != new HashSet<>(authorIds).size()) {
            throw new AuthorsNotFoundException();
        }

        book.setTitle(dto.title());
        book.setIsbn(dto.isbn());
        book.setAmount(dto.amount());
        book.setDatePublication(dto.datePublication());
        book.setCategory(category.get());
        book.setSupplier(supplier.get());
        book.setAuthors(new HashSet<>(foundAuthors));

        return this.bookRepository.save(book);
    }

    public List<Book> getByCategory(UUID categoryId) {
        List<Book> books = this.bookRepository.findByCategoryId(categoryId);

        if (books.isEmpty()) {
            throw new BooksNotFoundByCategoryIdException(categoryId);
        }

        return books;
    }

    public void delete(UUID id) {
        var book = this.getById(id);

        this.bookRepository.delete(book);
    }
}
