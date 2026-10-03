package com.example.apirestspringboot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Loan {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime loanDate;
    private LocalDateTime loanDateScheduled;
    private LocalDateTime loanDateReturn;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "loan_book",
            joinColumns = @JoinColumn(name = "loan_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
