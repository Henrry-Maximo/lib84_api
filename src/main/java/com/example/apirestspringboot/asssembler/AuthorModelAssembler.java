package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.AuthorController;
import com.example.apirestspringboot.entity.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorModelAssembler implements RepresentationModelAssembler<Author, EntityModel<Author>> {

    @Override
    public EntityModel<Author> toModel(Author author) {
        return EntityModel.of(author,
                linkTo(methodOn(AuthorController.class).getById(author.getId())).withSelfRel(),
                linkTo(methodOn(AuthorController.class).all(Pageable.unpaged())).withRel("all-authors"),
                linkTo(methodOn(AuthorController.class).update(null, author.getId())).withRel("update"),
                linkTo(methodOn(AuthorController.class).delete(author.getId())).withRel("delete"));
    }
}
