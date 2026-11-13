package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_loans")
@JsonPropertyOrder({"id", "loanDate", "loanDateScheduled", "loanDateReturn", "user", "books", "_links"})
public class Loan extends RepresentationModel<Loan> {

    @Id
    @GeneratedValue(generator = "UUID")
    @Schema(example = "2df59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @CreationTimestamp
    @Schema(example = "2026-11-13T00:00:00")
    private LocalDateTime loanDate;

    @NotNull
    @Column(nullable = false)
    @Schema(example = "2026-12-01T10:00:00")
    private LocalDateTime loanDateScheduled;

    @Schema(example = "2026-12-15T10:00:00")
    private LocalDateTime loanDateReturn;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotEmpty
    @ManyToMany
    @JoinTable(
            name = "tb_loan_book",
            joinColumns = @JoinColumn(name = "loan_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getLoanDateScheduled() {
        return loanDateScheduled;
    }

    public void setLoanDateScheduled(LocalDateTime loanDateScheduled) {
        this.loanDateScheduled = loanDateScheduled;
    }

    public LocalDateTime getLoanDateReturn() {
        return loanDateReturn;
    }

    public void setLoanDateReturn(LocalDateTime loanDateReturn) {
        this.loanDateReturn = loanDateReturn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
