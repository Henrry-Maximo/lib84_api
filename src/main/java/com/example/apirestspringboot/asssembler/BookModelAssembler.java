package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.BookController;
import com.example.apirestspringboot.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {

    @Override
    public EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book,
                linkTo(methodOn(BookController.class).getById(book.getId())).withRel("book"),
                linkTo(methodOn(BookController.class).all(Pageable.unpaged())).withRel("books"));
    }
}
