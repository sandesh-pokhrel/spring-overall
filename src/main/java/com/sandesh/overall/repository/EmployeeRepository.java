package com.sandesh.overall.repository;

import com.sandesh.overall.model.Employee;
import com.sandesh.overall.projection.EmployeeDTO;
import com.sandesh.overall.projection.IEmployee;
import com.sandesh.overall.projection.IEmployeeComplex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select e.name from Employee e where e.id=?1")
    String findNameById(Long id);

    // anything works here like findCustomById findHelloHelloById
    EmployeeDTO findDtoById(Long id);

    @Query("select new com.sandesh.overall.projection.EmployeeDTO(e.name, e.salary) from Employee e where e.id = ?1")
    EmployeeDTO findUsingQueryById(Long id);

    IEmployee findCustomById(Long id);

    IEmployeeComplex findCustomComplexById(Long id);
}
