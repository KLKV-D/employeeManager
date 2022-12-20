package ru.klokov.employee_manager.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.EmployeeRepository;
import ru.klokov.employee_manager.service.EmployeeService;

import java.util.ArrayList;
import java.util.List;


import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id=" + id));
    }

    @Override
    public Employee addEmployee(Employee employee) {
        if (employeeAlreadyExists(employee)) throw new ResourceAlreadyExistsException(
                "Employee with same name, birth date, employment date, position and department already exists!");
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee, Long id) {
        if (employeeAlreadyExists(employee)) throw new ResourceAlreadyExistsException(
                "Employee with same name, birth date, employment date, position and department already exists!");
        Employee employeeToUpdate = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id=" + id));
        employeeToUpdate.setFirstName(employee.getFirstName());
        employeeToUpdate.setLastName(employee.getLastName());
        employeeToUpdate.setBirthDate(employee.getBirthDate());
        employeeToUpdate.setEmploymentDate(employee.getEmploymentDate());
        employeeToUpdate.setPosition(employee.getPosition());
        employeeToUpdate.setDepartment(employee.getDepartment());
        return employeeRepository.save(employeeToUpdate);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id=" + id));
        employeeRepository.deleteById(id);
    }

    @Override
    public Long getNumberOfEmployees() {
        return employeeRepository.count();
    }

    @Override
    public List<Employee> get3LastEmployees() {
        return employeeRepository.findTop3ByOrderByIdDesc();
    }

    @Override
    public Page<Employee> getEmployeesPaginated(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return employeeRepository.findAll(pageable);
    }

    @Override
    public Page<Employee> getEmployeesByPosition(Position position, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Employee employee = new Employee();
        employee.setPosition(position);
        Example<Employee> example = Example.of(employee);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return employeeRepository.findAll(example, pageable);
    }

    @Override
    public Page<Employee> getEmployeesByDepartment(Department department, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Employee employee = new Employee();
        employee.setDepartment(department);
        Example<Employee> example = Example.of(employee);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return employeeRepository.findAll(example, pageable);
    }

    @Override
    public Boolean employeeAlreadyExists(Employee employee) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("firstName", ignoreCase())
                .withMatcher("lastName", ignoreCase());

        Example<Employee> example = Example.of(employee, matcher);
        Specification<Employee> specification = ((root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("birthDate"), employee.getBirthDate()));
            predicates.add(builder.equal(root.get("employmentDate"), employee.getEmploymentDate()));
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        });

        return employeeRepository.exists(specification);
    }
}
