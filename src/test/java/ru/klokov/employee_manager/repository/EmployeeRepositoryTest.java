package ru.klokov.employee_manager.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Position;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void EmployeeRepository_FindTop3ByOrderByIdDesc_ReturnTop3EmployeesOrderedByIdDesc() {
        Position position1 = Position.builder().name("pos1").build();
        Position position2 = Position.builder().name("pos2").build();
        Position position3 = Position.builder().name("pos3").build();
        Position position4 = Position.builder().name("pos4").build();

        positionRepository.save(position1);
        positionRepository.save(position2);
        positionRepository.save(position3);
        positionRepository.save(position4);

        Department department1 = Department.builder().name("dep1").build();
        Department department2 = Department.builder().name("dep2").build();
        Department department3 = Department.builder().name("dep3").build();
        Department department4 = Department.builder().name("dep4").build();

        departmentRepository.save(department1);
        departmentRepository.save(department2);
        departmentRepository.save(department3);
        departmentRepository.save(department4);

        Employee employee1 = Employee.builder().firstName("employee1").lastName("emp1")
                .position(position1).department(department1).build();
        Employee employee2 = Employee.builder().firstName("employee2").lastName("emp2")
                .position(position2).department(department2).build();
        Employee employee3 = Employee.builder().firstName("employee3").lastName("emp3")
                .position(position3).department(department3).build();
        Employee employee4 = Employee.builder().firstName("employee4").lastName("emp4")
                .position(position4).department(department4).build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);

        List<Employee> expectedEmployees = employeeRepository.findTop3ByOrderByIdDesc();

        Assertions.assertThat(expectedEmployees.size()).isEqualTo(3);
        Assertions.assertThat(expectedEmployees.get(0).getFirstName()).isEqualTo("employee4");
        Assertions.assertThat(expectedEmployees.get(1).getFirstName()).isEqualTo("employee3");
        Assertions.assertThat(expectedEmployees.get(2).getFirstName()).isEqualTo("employee2");
    }
}
