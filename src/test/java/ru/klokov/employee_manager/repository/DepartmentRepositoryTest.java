package ru.klokov.employee_manager.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.klokov.employee_manager.entity.Department;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DepartmentRepositoryTest {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void DepartmentRepository_FindTop3ByOrderByIdDesc_ReturnTop3DepartmentsOrderedByIdDesc() {
        Department department1 = Department.builder().name("department1").build();
        Department department2 = Department.builder().name("department2").build();
        Department department3 = Department.builder().name("department3").build();
        Department department4 = Department.builder().name("department4").build();

        departmentRepository.save(department1);
        departmentRepository.save(department2);
        departmentRepository.save(department3);
        departmentRepository.save(department4);

        List<Department> expectedDepartments = departmentRepository.findTop3ByOrderByIdDesc();

        Assertions.assertThat(expectedDepartments.size()).isEqualTo(3);
        Assertions.assertThat(expectedDepartments.get(0).getName()).isEqualTo("department4");
        Assertions.assertThat(expectedDepartments.get(1).getName()).isEqualTo("department3");
        Assertions.assertThat(expectedDepartments.get(2).getName()).isEqualTo("department2");
    }
}
