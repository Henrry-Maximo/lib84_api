package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.LoanController;
import com.example.apirestspringboot.entity.Loan;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoanModelAssembler implements RepresentationModelAssembler<Loan, EntityModel<Loan>> {

    @Override
    public EntityModel<Loan> toModel(Loan loan) {
        return EntityModel.of(loan,
                linkTo(methodOn(LoanController.class).getById(loan.getId())).withRel("loan"),
                linkTo(methodOn(LoanController.class).all(Pageable.unpaged())).withRel("loans"));
    }
}
