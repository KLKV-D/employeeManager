package ru.klokov.employee_manager.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.DepartmentRepository;
import ru.klokov.employee_manager.service.impl.DepartmentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;

    @BeforeEach
    public void setup() {
        department = Department.builder().id(1L).name("department").build();
    }

    @Test
    public void DepartmentService_AddDepartment_ReturnAddedDepartment() {
        when(departmentRepository.save(Mockito.any(Department.class))).thenReturn(department);

        Department expectedDepartment = departmentService.addDepartment(department);

        Assertions.assertThat(expectedDepartment).isNotNull();
        Assertions.assertThat(expectedDepartment.getName()).isEqualTo("department");
    }

    // TODO throw exception when add existing department

    @Test
    public void DepartmentService_GetAllDepartments_ReturnAllDepartments() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<Department> expectedDepartments = departmentService.getAllDepartments();

        Assertions.assertThat(expectedDepartments).isNotNull();
        Assertions.assertThat(expectedDepartments.size()).isEqualTo(1);
        Assertions.assertThat(expectedDepartments.get(0).getName()).isEqualTo("department");
    }

    @Test
    public void DepartmentService_GetDepartmentById_ReturnDepartmentById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department expectedDepartment = departmentService.getDepartmentById(department.getId());

        Assertions.assertThat(expectedDepartment).isNotNull();
        Assertions.assertThat(expectedDepartment.getName()).isEqualTo("department");
    }

    @Test
    public void DepartmentService_GetDepartmentById_ThrowsResourceNotFoundException() {
        when(departmentRepository.findById(department.getId()))
                .thenThrow(new ResourceNotFoundException("Department not found with id=" + department.getId()));

        Assertions.assertThatThrownBy(() -> departmentService.getDepartmentById(department.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department not found with id=" + department.getId());
    }

    @Test
    public void DepartmentService_UpdateDepartment_ReturnUpdatedDepartment() {
        Department newDepartment = Department.builder().id(1L).name("newDepartment").build();
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(newDepartment);

        Department expectedUpdatedDepartment = departmentService.updateDepartment(newDepartment, 1L);

        Assertions.assertThat(expectedUpdatedDepartment).isNotNull();
        Assertions.assertThat(expectedUpdatedDepartment.getName()).isEqualTo(newDepartment.getName());
    }

    @Test
    public void DepartmentService_DeleteById_ReturnNothing() {
        Long id = 1L;

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        doNothing().when(departmentRepository).deleteById(id);

        assertAll(() -> departmentService.deleteDepartment(id));
    }

    @Test
    public void DepartmentService_GetNumberOfDepartments_ReturnNumberOfDepartments() {
        when(departmentRepository.count()).thenReturn(1L);

        Long expectedNumberOfDepartments = departmentService.getNumberOfDepartments();

        Assertions.assertThat(expectedNumberOfDepartments).isEqualTo(1L);
    }

    @Test
    public void DepartmentService_GetTop3LastDepartments_ReturnTop3LastAddedDepartments() {
        Department department2 = Department.builder().id(2L).name("department2").build();
        Department department3 = Department.builder().id(3L).name("department3").build();
        Department department4 = Department.builder().id(4L).name("department4").build();

        when(departmentRepository.findTop3ByOrderByIdDesc()).thenReturn(List.of(department4, department3, department2));

        List<Department> expectedDepartments = departmentService.get3LastDepartments();

        Assertions.assertThat(expectedDepartments).isNotNull();
        Assertions.assertThat(expectedDepartments.size()).isEqualTo(3);
    }

    @Test
    public void DepartmentService_GetDepartmentsPaginated_ReturnDepartmentsPaginated() {
        Page<Department> page = Mockito.mock(Page.class);

        when(departmentRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Department> expectedPage = departmentService.getDepartmentsPaginated(1, 5, "id", "asc");

        Assertions.assertThat(expectedPage).isNotNull();
    }

    @Test
    public void DepartmentService_DepartmentAlreadyExists_CheckIfDepartmentWithGivenNameAlreadyExists() {
        when(departmentRepository.exists(Mockito.any(Example.class))).thenReturn(true);

        Assertions.assertThat(departmentService.departmentAlreadyExists(department)).isTrue();
    }
}
