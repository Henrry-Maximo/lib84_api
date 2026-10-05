package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.CategoryController;
import com.example.apirestspringboot.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<Category>> {

    @Override
    public EntityModel<Category> toModel(Category category) {
        return EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getById(category.getId())).withRel("category"),
                linkTo(methodOn(CategoryController.class).all(Pageable.unpaged())).withRel("categories"));
    }
}
