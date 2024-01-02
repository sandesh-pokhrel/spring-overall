package com.sandesh.overall.service;

import com.sandesh.overall.model.Employee;
import com.sandesh.overall.projection.EmployeeDTO;
import com.sandesh.overall.projection.IEmployee;
import com.sandesh.overall.projection.IEmployeeComplex;
import com.sandesh.overall.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public List<Employee> getAll() {
        return this.employeeRepository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        return this.employeeRepository.findById(id);
    }

    public String getNameById(Long id) { return this.employeeRepository.findNameById(id); }

    public EmployeeDTO getNameAndSalaryById(Long id) { return this.employeeRepository.findDtoById(id); }

    public EmployeeDTO getNameAndSalaryUsingQueryById(Long id) { return this.employeeRepository.findUsingQueryById(id); }

    public IEmployee getNameAndSalaryUsingInterfaceById(Long id) { return this.employeeRepository.findCustomById(id); }

    public IEmployeeComplex getNameAndSalaryUsingInterfaceComplexById(Long id) { return this.employeeRepository.findCustomComplexById(id); }
}
