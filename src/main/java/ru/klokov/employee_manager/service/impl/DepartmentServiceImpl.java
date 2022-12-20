package ru.klokov.employee_manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.DepartmentRepository;
import ru.klokov.employee_manager.service.DepartmentService;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id=" + id));
    }

    @Override
    public Department addDepartment(Department department) {
        if (departmentAlreadyExists(department)) throw new ResourceAlreadyExistsException(
                "Department with name " + department.getName() + " already exists!");

        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Department department, Long id) {
        if (departmentAlreadyExists(department)) throw new ResourceAlreadyExistsException(
                "Department with name " + department.getName() + " already exists!");

        Department departmentToUpdate = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id=" + id));

        departmentToUpdate.setName(department.getName());
        departmentToUpdate.setEmployees(department.getEmployees());
        return departmentRepository.save(departmentToUpdate);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id=" + id));
        departmentRepository.deleteById(id);
    }

    @Override
    public Long getNumberOfDepartments() {
        return departmentRepository.count();
    }

    @Override
    public List<Department> get3LastDepartments() {
        return departmentRepository.findTop3ByOrderByIdDesc();
    }

    @Override
    public Page<Department> getDepartmentsPaginated(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return departmentRepository.findAll(pageable);
    }

    @Override
    public Boolean departmentAlreadyExists(Department department) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("name", ignoreCase());
        Example<Department> example = Example.of(department, matcher);
        return departmentRepository.exists(example);
    }
}