package com.sandesh.overall.controller;

import com.github.javafaker.Faker;
import com.sandesh.overall.config.KafkaConfig;
import com.sandesh.overall.model.Employee;
import com.sandesh.overall.projection.EmployeeDTO;
import com.sandesh.overall.projection.IEmployee;
import com.sandesh.overall.projection.IEmployeeComplex;
import com.sandesh.overall.service.EmployeeService;
import com.sandesh.overall.util.GenericUtil;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;
    private final KafkaTemplate<Long, Employee> employeeKafkaTemplate;
    private final KafkaTemplate<Long, String> namesKafkaTemplate;
    private final Timer timer;
    private final Counter counter;
    private final ObservationRegistry observationRegistry;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              MeterRegistry meterRegistry,
                              ObservationRegistry observationRegistry,
                              @Qualifier("employeeKafkaTemplate") KafkaTemplate<Long, Employee> employeeKafkaTemplate,
                              @Qualifier("namesKafkaTemplate") KafkaTemplate<Long, String> namesKafkaTemplate) {
        this.employeeService = employeeService;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
        this.namesKafkaTemplate = namesKafkaTemplate;
        this.observationRegistry = observationRegistry;
        this.timer = meterRegistry.timer("hello.method.timer");
        this.counter = meterRegistry.counter("hello.method.counter");
    }

    @GetMapping("/hello")
    public String sayHello() {
        counter.increment();
        log.warn("Test warn message----");
        log.error("Test error message----");
        return "hello";
    }

    @GetMapping("/hello-delayed")
    public String sayHelloDelayed() {
        timer.record(() -> GenericUtil.sleep(2000));
        return "hello";
    }

    @GetMapping("/hello-timed")
    @Timed(value = "hello.method.timer.aspect") // With this aop is used internally for timing
    public String sayHelloTimed() {
        GenericUtil.sleep(2000);
        return "hello";
    }

    @GetMapping("/hello-observe")
    public String sayHelloObserve() {
        Observation observation = Observation.createNotStarted("hello.method.observe", observationRegistry);
        return observation.observe(() -> {
            GenericUtil.sleep(3000);
            return "hello";
        });
    }

    @GetMapping("/hello-observe-aspect")
    @Observed(name = "hello.method.observe.aspect") // With this aop is used internally for observing
    public String sayHelloObserveAspect() {
        GenericUtil.sleep(3000);
        return "hello";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/generate-names")
    @SneakyThrows
    public void generateNames() {
        Faker faker = new Faker();
        IntStream.range(0, 10).forEach(a -> {
            namesKafkaTemplate.send(KafkaConfig.NAMES_TOPIC_NAME, faker.funnyName().name());
            GenericUtil.sleep(2000);
        });
    }

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return this.employeeService.getAll();
    }

    @GetMapping("/employees/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return this.employeeService.getById(id).orElse(null);
    }

    @GetMapping("/employees/{id}/hateoas")
    public EntityModel<Employee> getEmployeeByIdHateoas(@PathVariable Long id) {
        Employee employee = this.employeeService.getById(id).orElse(null);
        Link selfLink = linkTo(methodOn(EmployeeController.class).getEmployeeByIdHateoas(id)).withSelfRel();
        Link selfLinkNameOnly = GenericUtil.buildLinkFromContextPath("self_name_only", "/employees/{id}/name", id);
        Link aggregateRoot = linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees");
        assert employee != null;
        return EntityModel.of(employee, selfLink, selfLinkNameOnly, aggregateRoot);
    }

    @GetMapping("/employees-publish")
    public List<Employee> getAllEmployeesAndPublish() {
        List<Employee> employees = this.employeeService.getAll();
        employees.forEach(emp -> employeeKafkaTemplate.send(KafkaConfig.EMPLOYEE_TOPIC_NAME, emp));
        return employees;
    }

    @GetMapping("/employees-publish-join-test")
    public List<Employee> getAllEmployeesAndPublishForJoin() {
        List<Employee> employees = this.employeeService.getAll();
        employees.forEach(emp -> {
            employeeKafkaTemplate.send(KafkaConfig.EMPLOYEE_TOPIC_NAME, emp.getId(), emp);
            employeeKafkaTemplate.send(KafkaConfig.EMPLOYEE_TOPIC_NAME_2, emp.getId(), emp);
        });
        return employees;
    }

    @GetMapping("/employees/{id}/name")
    public String getEmployeeNameById(@PathVariable Long id) {
        return this.employeeService.getNameById(id);
    }

    @GetMapping("/employees/{id}/name-salary")
    public EmployeeDTO getEmployeeNameAndSalaryById(@PathVariable Long id) {
        return this.employeeService.getNameAndSalaryById(id);
    }

    @GetMapping("/employees/{id}/name-salary-query")
    public EmployeeDTO getEmployeeNameAndSalaryUsingQueryById(@PathVariable Long id) {
        return this.employeeService.getNameAndSalaryUsingQueryById(id);
    }

    @GetMapping("/employees/{id}/name-salary-interface")
    public IEmployee getEmployeeNameAndSalaryUsingInterfaceById(@PathVariable Long id) {
        return this.employeeService.getNameAndSalaryUsingInterfaceById(id);
    }

    @GetMapping("/employees/{id}/name-salary-interface-complex")
    public IEmployeeComplex getEmployeeNameAndSalaryUsingInterfaceComplexById(@PathVariable Long id) {
        return this.employeeService.getNameAndSalaryUsingInterfaceComplexById(id);
    }
}
