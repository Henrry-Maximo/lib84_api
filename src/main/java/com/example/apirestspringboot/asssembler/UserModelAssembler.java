package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.UserController;
import com.example.apirestspringboot.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).all(Pageable.unpaged())).withRel("all-users"),
                linkTo(methodOn(UserController.class).updateAllFields(null, user.getId())).withRel("update"),
                linkTo(methodOn(UserController.class).delete(user.getId())).withRel("delete"));
    }

}
