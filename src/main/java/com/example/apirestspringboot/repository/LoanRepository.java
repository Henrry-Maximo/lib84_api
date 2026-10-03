package com.example.apirestspringboot.repository;

import com.example.apirestspringboot.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
}
