package ru.klokov.employee_manager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.klokov.employee_manager.dto.UpdatePasswordDto;
import ru.klokov.employee_manager.dto.UserDto;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).username("admin").password("admin").role(Role.ADMIN).enabled(true).build();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_GetAllUsersPaginated_ReturnAllUsersPaginated() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.getUsersWithAllRolesPaginated(1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));
        when(userService.getUsersWithUserRolePaginated(1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/users/page/{pageNumber}", 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/all-users"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_GetUserProfile_ReturnProfileOfUserWithGivenId() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);

        this.mockMvc.perform(get("/api/users/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-profile"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_AddUserForm_ReturnUserForm() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);

        this.mockMvc.perform(get("/api/users/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add-user"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("userDto"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_AddUser_ReturnAddedUser() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.addUser(Mockito.any(User.class))).thenReturn(user);

        this.mockMvc.perform(post("/api/users/add")
                        .flashAttr("userDto", new UserDto(user)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/users/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_GetUpdateUserProfileForm_ReturnUpdateUserProfileForm() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);

        this.mockMvc.perform(get("/api/users/edit/profile/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit-users-profile"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("userDto"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_UpdateUserProfile_ReturnUpdatedUser() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.updateUserProfile(Mockito.any(User.class), Mockito.any(Long.class))).thenReturn(user);

        this.mockMvc.perform(post("/api/users/edit/profile/{id}", 1)
                        .flashAttr("userDto", new UserDto(user)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/home"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_GetUpdateUserPasswordForm_ReturnUpdateUserPasswordForm() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);

        this.mockMvc.perform(get("/api/users/edit/password/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit-users-password"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("updatePasswordDto"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_UpdateUserPassword_ReturnUserWithUpdatedPassword() throws Exception {
        when(userService.getUserByUsername(Mockito.any(String.class))).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.updateUsersPassword(Mockito.any(UpdatePasswordDto.class), Mockito.any(Long.class)))
                .thenReturn(user);

        this.mockMvc.perform(post("/api/users/edit/password/{id}", 1)
                        .flashAttr("updatePasswordDto", new UpdatePasswordDto("123", "123"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/home"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void UserController_DeleteUser_ReturnNothing() throws Exception {
        this.mockMvc.perform(get("/api/users/delete/{id}", 1L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/users/page/1?sortField=id&sortDirection=asc"));
    }
}
