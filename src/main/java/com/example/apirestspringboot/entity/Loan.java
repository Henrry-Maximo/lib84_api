package com.example.apirestspringboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "tb_loans")
public class Loan extends RepresentationModel<Loan> {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @CreationTimestamp
    private LocalDateTime loanDate;

    private LocalDateTime loanDateScheduled;
    private LocalDateTime loanDateReturn;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
