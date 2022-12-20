package ru.klokov.employee_manager.service;

import org.springframework.data.domain.Page;
import ru.klokov.employee_manager.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department addDepartment(Department department);
    Department updateDepartment(Department department, Long id);
    void deleteDepartment(Long id);
    Long getNumberOfDepartments();
    List<Department> get3LastDepartments();
    Page<Department> getDepartmentsPaginated(int pageNumber, int pageSize, String sortField, String sortDirection);
    Boolean departmentAlreadyExists(Department department);
}
