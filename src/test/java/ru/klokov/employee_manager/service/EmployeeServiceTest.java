package ru.klokov.employee_manager.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.EmployeeRepository;
import ru.klokov.employee_manager.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    public void setup() {
        Position position = Position.builder().id(1L).name("position").build();
        Department department = Department.builder().id(1L).name("department").build();
        employee = Employee.builder().id(1L).firstName("firstName").lastName("lastName")
                .position(position).department(department).build();
    }

    @Test
    public void EmployeeService_AddEmployee_ReturnAddedEmployee() {
        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        Employee expectedEmployee = employeeService.addEmployee(employee);

        Assertions.assertThat(expectedEmployee).isNotNull();
        Assertions.assertThat(expectedEmployee.getFirstName()).isEqualTo("firstName");
    }

    // TODO throw exception when add existing employee

    @Test
    public void EmployeeService_GetAllEmployees_ReturnAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<Employee> expectedEmployees = employeeService.getAllEmployees();

        Assertions.assertThat(expectedEmployees).isNotNull();
        Assertions.assertThat(expectedEmployees.size()).isEqualTo(1);
        Assertions.assertThat(expectedEmployees.get(0).getFirstName()).isEqualTo("firstName");
    }

    @Test
    public void EmployeeService_GetEmployeeById_ReturnEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee expectedEmployee = employeeService.getEmployeeById(employee.getId());

        Assertions.assertThat(expectedEmployee).isNotNull();
        Assertions.assertThat(expectedEmployee.getFirstName()).isEqualTo("firstName");
    }

    @Test
    public void EmployeeService_GetEmployeeById_ThrowsResourceNotFoundException() {
        when(employeeRepository.findById(employee.getId()))
                .thenThrow(new ResourceNotFoundException("Employee not found with id=" + employee.getId()));

        Assertions.assertThatThrownBy(() -> employeeService.getEmployeeById(employee.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id=" + employee.getId());
    }

    @Test
    public void EmployeeService_UpdateEmployee_ReturnUpdatedEmployee() {
        Employee newEmployee = Employee.builder().id(1L).firstName("newFirstName").build();
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(newEmployee);

        Employee expectedUpdatedEmployee = employeeService.updateEmployee(newEmployee, 1L);

        Assertions.assertThat(expectedUpdatedEmployee).isNotNull();
        Assertions.assertThat(expectedUpdatedEmployee.getFirstName()).isEqualTo(newEmployee.getFirstName());
    }

    @Test
    public void EmployeeService_DeleteById_ReturnNothing() {
        Long id = 1L;

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        doNothing().when(employeeRepository).deleteById(id);

        assertAll(() -> employeeService.deleteEmployee(id));
    }

    @Test
    public void EmployeeService_GetNumberOfEmployees_ReturnNumberOfEmployees() {
        when(employeeRepository.count()).thenReturn(1L);

        Long expectedNumberOfEmployees = employeeService.getNumberOfEmployees();

        Assertions.assertThat(expectedNumberOfEmployees).isEqualTo(1L);
    }

    @Test
    public void EmployeeService_GetTop3LastEmployees_ReturnTop3LastAddedEmployees() {
        Employee employee2 = Employee.builder().id(2L).firstName("firstName2").build();
        Employee employee3 = Employee.builder().id(3L).firstName("firstName3").build();
        Employee employee4 = Employee.builder().id(4L).firstName("firstName4").build();

        when(employeeRepository.findTop3ByOrderByIdDesc()).thenReturn(List.of(employee4, employee3, employee2));

        List<Employee> expectedEmployees = employeeService.get3LastEmployees();

        Assertions.assertThat(expectedEmployees).isNotNull();
        Assertions.assertThat(expectedEmployees.size()).isEqualTo(3);
    }

    @Test
    public void EmployeeService_GetEmployeesPaginated_ReturnEmployeesPaginated() {
        Page<Employee> page = Mockito.mock(Page.class);

        when(employeeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Employee> expectedPage = employeeService.getEmployeesPaginated(1, 5, "id", "asc");

        Assertions.assertThat(expectedPage).isNotNull();
    }

    @Test
    public void EmployeeService_EmployeeAlreadyExists_ReturnTrueIfGivenEmployeeAlreadyExists() {
        when(employeeRepository.exists(Mockito.any(Specification.class))).thenReturn(true);

        Assertions.assertThat(employeeService.employeeAlreadyExists(employee)).isTrue();
    }
}
