package ru.klokov.employee_manager.service;

import org.springframework.data.domain.Page;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Position;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee addEmployee(Employee employee);
    Employee updateEmployee(Employee employee, Long id);
    void deleteEmployee(Long id);
    Long getNumberOfEmployees();
    List<Employee> get3LastEmployees();
    Page<Employee> getEmployeesPaginated(int pageNumber, int pageSize, String sortField, String sortDirection);
    Page<Employee> getEmployeesByPosition(Position position, int pageNumber, int pageSize, String sortField, String sortDirection);
    Page<Employee> getEmployeesByDepartment(Department department, int pageNumber, int pageSize, String sortField, String sortDirection);
    Boolean employeeAlreadyExists(Employee employee);
}
