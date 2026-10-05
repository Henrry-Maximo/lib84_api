package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.LoanRecordDto;
import com.example.apirestspringboot.entity.Loan;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.BookRepository;
import com.example.apirestspringboot.repository.LoanRepository;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Loan create(LoanRecordDto dto) {
        var loan = new Loan();

        loan.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new UserNotFoundException(dto.userId())));
        loan.setBooks(new HashSet<>(bookRepository.findAllById(dto.bookIds())));
        loan.setLoanDateScheduled(dto.loanDateScheduled());

        return this.loanRepository.save(loan);
    }

    public Page<Loan> getAll(Pageable pageable) {
        return this.loanRepository.findAll(pageable);
    }

    public Loan getById(UUID id) {
        return this.loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }

    public Loan update(Loan loan, LoanRecordDto dto) {
        loan.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new UserNotFoundException(dto.userId())));
        loan.setBooks(new HashSet<>(bookRepository.findAllById(dto.bookIds())));
        loan.setLoanDateScheduled(dto.loanDateScheduled());

        return this.loanRepository.save(loan);
    }

    public List<Loan> getByUser(UUID userId) {
        return this.loanRepository.findByUserId(userId);
    }

    public void delete(Loan loan) {
        this.loanRepository.deleteById(loan.getId());
    }
}
