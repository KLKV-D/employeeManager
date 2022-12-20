package ru.klokov.employee_manager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.service.impl.DepartmentServiceImpl;
import ru.klokov.employee_manager.service.impl.EmployeeServiceImpl;
import ru.klokov.employee_manager.service.impl.PositionServiceImpl;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private PositionServiceImpl positionService;
    @MockBean
    private DepartmentServiceImpl departmentService;
    @MockBean
    private EmployeeServiceImpl employeeService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void HomeController_GetHomePage_ReturnHomePage() throws Exception {
        User user = User.builder().id(1L).username("admin").password("admin").role(Role.ADMIN).build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);

        this.mockMvc.perform(get("/api/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("managers"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("numberOfUsers"))
                .andExpect(model().attributeExists("numberOfUsersAndManagers"))
                .andExpect(model().attributeExists("numberOfPositions"))
                .andExpect(model().attributeExists("numberOfDepartments"))
                .andExpect(model().attributeExists("numberOfEmployees"));
    }
}
