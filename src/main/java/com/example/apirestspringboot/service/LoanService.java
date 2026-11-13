package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.LoanRecordDto;
import com.example.apirestspringboot.entity.Loan;
import com.example.apirestspringboot.exception.BooksNotFoundException;
import com.example.apirestspringboot.exception.LoanNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.BookRepository;
import com.example.apirestspringboot.repository.LoanRepository;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Loan create(LoanRecordDto dto) {
        var userId = dto.userId();
        var bookIds = dto.bookIds();

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        var foundBooks = bookRepository.findAllById(bookIds);
        if (foundBooks.size() != new HashSet<>(bookIds).size()) {
            throw new BooksNotFoundException();
        }

        var loan = new Loan();

        loan.setUser(user.get());
        loan.setBooks(new HashSet<>(foundBooks));
        loan.setLoanDateScheduled(dto.loanDateScheduled());

        return this.loanRepository.save(loan);
    }

    public Page<Loan> getAll(Pageable pageable) {
        return this.loanRepository.findAll(pageable);
    }

    public Loan getById(UUID id) {
        var loan = this.loanRepository.findById(id);

        if (loan.isEmpty()) {
            throw new LoanNotFoundException(id);
        }

        return loan.get();
    }

    public List<Loan> getByUser(UUID userId) {
        var user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return this.loanRepository.findByUserId(userId);
    }

    @Transactional
    public Loan update(UUID id, LoanRecordDto dto) {
        var loan = this.getById(id);

        var userId = dto.userId();
        var bookIds = dto.bookIds();

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        var foundBooks = bookRepository.findAllById(bookIds);
        if (foundBooks.size() != new HashSet<>(bookIds).size()) {
            throw new BooksNotFoundException();
        }

        loan.setUser(user.get());
        loan.setBooks(new HashSet<>(foundBooks));
        loan.setLoanDateScheduled(dto.loanDateScheduled());

        return this.loanRepository.save(loan);
    }

    @Transactional
    public void delete(UUID id) {
        var loan = this.getById(id);

        this.loanRepository.delete(loan);
    }
}
