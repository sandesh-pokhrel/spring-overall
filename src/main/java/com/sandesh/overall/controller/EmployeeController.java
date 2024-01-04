package com.sandesh.overall.controller;

import com.github.javafaker.Faker;
import com.sandesh.overall.config.KafkaConfig;
import com.sandesh.overall.model.Employee;
import com.sandesh.overall.projection.EmployeeDTO;
import com.sandesh.overall.projection.IEmployee;
import com.sandesh.overall.projection.IEmployeeComplex;
import com.sandesh.overall.service.EmployeeService;
import com.sandesh.overall.util.GenericUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;
    private final KafkaTemplate<Long, Employee> employeeKafkaTemplate;
    private final KafkaTemplate<Long, String> namesKafkaTemplate;

    public EmployeeController(EmployeeService employeeService,
                              @Qualifier("employeeKafkaTemplate") KafkaTemplate<Long, Employee> employeeKafkaTemplate,
                              @Qualifier("namesKafkaTemplate") KafkaTemplate<Long, String> namesKafkaTemplate) {
        this.employeeService = employeeService;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
        this.namesKafkaTemplate = namesKafkaTemplate;
    }

    @GetMapping("/hello")
    public String sayHello() {
        log.warn("Test warn message----");
        log.error("Test error message----");
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
