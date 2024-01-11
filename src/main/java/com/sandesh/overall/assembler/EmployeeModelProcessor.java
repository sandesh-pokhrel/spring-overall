package com.sandesh.overall.assembler;

import com.sandesh.overall.model.Employee;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This can be used to add some more links to EntityMode<Employee>
 * This runs after assembler completes its task
 * If the controller class does not return EntityModel using assembler but just empty EntityModel then also this can add links to them (NOTE)
 */
@Component
public class EmployeeModelProcessor implements RepresentationModelProcessor<EntityModel<Employee>> {
    @Override
    public @NotNull EntityModel<Employee> process(@NotNull EntityModel<Employee> model) {
        model.add(Link.of("/external-service/{customerId}")
                .withRel("external-service").expand(Objects.requireNonNull(model.getContent()).getId()));
        return model;
    }
}
