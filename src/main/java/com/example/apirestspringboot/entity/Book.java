package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity()
@Table(name = "tb_books") // nome da tabela no banco de dados
@JsonPropertyOrder({"id", "title", "isbn", "amount","datePublication", "createdAt", "category", "authors", "supplier", "_links"})
public class Book extends RepresentationModel<Book> {

    @Id
    @GeneratedValue(generator = "UUID")
    @Schema(example = "2df59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false, length = 255)
    @Schema(example = "Eu, robô")
    private String title;

    @NotBlank
    @Size(min = 3, max = 17)
    @Column(nullable = false, unique = true, length = 100)
    @Schema(example = "9780132350884")
    private String isbn;

    @NotNull
    @Min(value = 0, message = "must be have more one character")
    @Column(nullable = false)
    @Schema(example = "30")
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    @Schema(example = "2026-11-13")
    private LocalDate datePublication;

    @CreationTimestamp
    @Schema(example = "2026-11-13T00:00:000Z")
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(example = "3ac59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private Category category;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @Schema(example = "1bd59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private Supplier supplier;

    @NotNull
    @ManyToMany
    @JoinTable(
            name = "tb_book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
