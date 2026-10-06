package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.ProfileController;
import com.example.apirestspringboot.entity.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProfileModelAssembler implements RepresentationModelAssembler<Profile, EntityModel<Profile>> {

    @Override
    public EntityModel<Profile> toModel(Profile profile) {
        return EntityModel.of(profile,
                linkTo(methodOn(ProfileController.class).getById(profile.getId())).withSelfRel(),
                linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("all-profiles"),
                linkTo(methodOn(ProfileController.class).update(null, profile.getId())).withRel("update"),
                linkTo(methodOn(ProfileController.class).delete(profile.getId())).withRel("delete"));
    }
}
