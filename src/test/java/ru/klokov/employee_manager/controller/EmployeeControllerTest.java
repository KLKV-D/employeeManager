package ru.klokov.employee_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.klokov.employee_manager.dto.EmployeeDto;
import ru.klokov.employee_manager.entity.*;
import ru.klokov.employee_manager.service.impl.DepartmentServiceImpl;
import ru.klokov.employee_manager.service.impl.EmployeeServiceImpl;
import ru.klokov.employee_manager.service.impl.PositionServiceImpl;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @MockBean
    private PositionServiceImpl positionService;
    @MockBean
    private DepartmentServiceImpl departmentService;
    @MockBean
    private EmployeeServiceImpl employeeService;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Department department;
    private Position position;
    private Employee employee;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).username("admin").password("admin").role(Role.ADMIN).build();
        department = Department.builder().id(1L).name("department").build();
        position = Position.builder().id(1L).name("position").build();
        employee = Employee.builder().id(1L).firstName("firstName").lastName("lastName")
                .position(position).department(department).build();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_GetAllEmployees_ReturnAllEmployees() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(employeeService.getEmployeesPaginated(1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/employees/page/{pageNumber}", 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/all-employees"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_GetEmployeeById_ReturnEmployeeById() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        this.mockMvc.perform(get("/api/employees/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/one-employee"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_AddEmployeeForm_ReturnAddEmployeeForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);

        this.mockMvc.perform(get("/api/employees/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/add-employee"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("employeeDto"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_AddEmployee_ReturnAddedEmployee() throws Exception {
        Position position2 = Position.builder().id(2L).name("position2").build();
        Department department2 = Department.builder().id(2L).name("department2").build();
        Employee newEmployee = Employee.builder().id(2L).firstName("firstName2").lastName("lastName2")
                .birthDate(new Date()).employmentDate(new Date()).position(position2).department(department2).build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(employeeService.addEmployee(newEmployee)).thenReturn(newEmployee);

        this.mockMvc.perform(post("/api/employees/add")
                        .flashAttr("employeeDto", new EmployeeDto(newEmployee)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/employees/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_EditEmployeeForm_ReturnEditEmployeeForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        this.mockMvc.perform(get("/api/employees/edit/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/edit-employee"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("employeeDto"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_EditEmployee_ReturnEditedEmployee() throws Exception {
        Position position2 = Position.builder().id(2L).name("position2").build();
        Department department2 = Department.builder().id(2L).name("department2").build();
        Employee newEmployee = Employee.builder().id(2L).firstName("firstName2").lastName("lastName2")
                .birthDate(new Date()).employmentDate(new Date()).position(position2).department(department2).build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(employeeService.updateEmployee(newEmployee, 2L)).thenReturn(newEmployee);

        this.mockMvc.perform(post("/api/employees/edit/{id}", 2)
                        .flashAttr("employeeDto", new EmployeeDto(newEmployee)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/employees/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_DeleteEmployee_ReturnNothing() throws Exception {
        this.mockMvc.perform(get("/api/employees/delete/{id}", 1L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/employees/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_GetEmployeesByPosition_ReturnAllEmployeesWithGivenPosition() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.getPositionById(1L)).thenReturn(position);
        when(employeeService.getEmployeesByPosition(position, 1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/employees/position/{positionId}/page/{pageNumber}", 1L, 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/all-employees"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void EmployeeController_GetEmployeesByDepartment_ReturnAllEmployeesWithGivenDepartment() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);
        when(employeeService.getEmployeesByDepartment(department, 1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/employees/department/{departmentId}/page/{pageNumber}", 1L, 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employee/all-employees"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }
}
