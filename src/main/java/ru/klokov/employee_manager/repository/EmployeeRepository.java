package ru.klokov.employee_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import ru.klokov.employee_manager.entity.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        QueryByExampleExecutor<Employee>, JpaSpecificationExecutor<Employee> {
    List<Employee> findTop3ByOrderByIdDesc();
}
