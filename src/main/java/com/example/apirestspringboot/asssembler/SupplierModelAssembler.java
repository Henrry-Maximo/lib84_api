package com.example.apirestspringboot.asssembler;

import com.example.apirestspringboot.controller.SupplierController;
import com.example.apirestspringboot.entity.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SupplierModelAssembler implements RepresentationModelAssembler<Supplier, EntityModel<Supplier>> {

    @Override
    public EntityModel<Supplier> toModel(Supplier supplier) {
        return EntityModel.of(supplier,
                linkTo(methodOn(SupplierController.class).getById(supplier.getId())).withSelfRel(),
                linkTo(methodOn(SupplierController.class).all(Pageable.unpaged())).withRel("all-suppliers"),
                linkTo(methodOn(SupplierController.class).update(null, supplier.getId())).withRel("update"),
                linkTo(methodOn(SupplierController.class).delete(supplier.getId())).withRel("delete"));
    }
}
