package com.sandesh.overall.assembler;

import com.sandesh.overall.controller.EmployeeController;
import com.sandesh.overall.model.Employee;
import com.sandesh.overall.util.GenericUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Instead of writing the code to add links in controller class, better to create an assembler for that
 * This can be autowired in controller class to create the links for Employee object
 */
@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

    @Override
    public @NotNull EntityModel<Employee> toModel(@NotNull Employee entity) {
        EntityModel<Employee> employeeEntityModel = EntityModel.of(entity);
        Link selfLink = linkTo(methodOn(EmployeeController.class).getEmployeeByIdHateoas(entity.getId())).withSelfRel();
        Link selfLinkNameOnly = GenericUtil.buildLinkFromContextPath("self_name_only", "/employees/{id}/name", entity.getId());
        Link aggregateRoot = linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees");
        employeeEntityModel.add(selfLink, selfLinkNameOnly, aggregateRoot);
        return employeeEntityModel;
    }

    @Override
    public @NotNull CollectionModel<EntityModel<Employee>> toCollectionModel(@NotNull Iterable<? extends Employee> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
