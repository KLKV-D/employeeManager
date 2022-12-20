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
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.service.impl.DepartmentServiceImpl;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {
    @MockBean
    private DepartmentServiceImpl departmentService;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Department department;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).username("admin").password("admin").role(Role.ADMIN).build();
        department = Department.builder().id(1L).name("department").build();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_GetAllDepartments_ReturnAllDepartments() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.getDepartmentsPaginated(1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/departments/page/{pageNumber}", 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("department/all-departments"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_GetDepartmentById_ReturnDepartmentById() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        this.mockMvc.perform(get("/api/departments/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("department/one-department"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_AddDepartmentForm_ReturnAddDepartmentForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);

        this.mockMvc.perform(get("/api/departments/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("department/add-department"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_AddDepartment_ReturnAddedDepartment() throws Exception {
        Department newDepartment = Department.builder().id(2L).name("department2").build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.addDepartment(newDepartment)).thenReturn(newDepartment);

        this.mockMvc.perform(post("/api/departments/add")
                .flashAttr("department", newDepartment).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/departments/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_EditDepartmentForm_ReturnEditDepartmentForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        this.mockMvc.perform(get("/api/departments/edit/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("department/edit-department"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_EditDepartment_ReturnEditDepartment() throws Exception {
        Department newDepartment = Department.builder().id(2L).name("department2").build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(departmentService.updateDepartment(newDepartment, 1L)).thenReturn(newDepartment);

        this.mockMvc.perform(post("/api/departments/edit/{id}", 1)
                        .flashAttr("department", newDepartment).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/departments/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void DepartmentController_DeleteDepartment_ReturnNothing() throws Exception {
        this.mockMvc.perform(get("/api/departments/delete/{id}", 1L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/departments/page/1?sortField=id&sortDirection=asc"));
    }
}
