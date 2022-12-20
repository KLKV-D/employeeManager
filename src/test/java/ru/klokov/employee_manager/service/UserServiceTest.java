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
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.UserRepository;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).username("user").password("user").role(Role.USER).enabled(true).build();
    }

    @Test
    public void UserService_addUser_ReturnAddedUser() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User expectedUser = userService.addUser(user);

        Assertions.assertThat(expectedUser).isNotNull();
        Assertions.assertThat(expectedUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void UserService_GetUsersWithAllRolesPaginated_ReturnUsersWithAllRolesPaginated() {
        Page<User> page = Mockito.mock(Page.class);

        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<User> expectedPage = userService.getUsersWithAllRolesPaginated(1, 5, "id", "asc");

        Assertions.assertThat(expectedPage).isNotNull();
    }

    @Test
    public void UserService_GetUsersWithUserRolePaginated_ReturnUsersWithUserRolePaginated() {
        Page<User> page = Mockito.mock(Page.class);

        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<User> expectedPage = userService.getUsersWithAllRolesPaginated(1, 5, "id", "asc");

        Assertions.assertThat(expectedPage).isNotNull();
    }

    @Test
    public void UserService_GetUserById_ReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User expectedUser = userService.getUserById(user.getId());

        Assertions.assertThat(expectedUser).isNotNull();
        Assertions.assertThat(expectedUser.getUsername()).isEqualTo("user");
    }

    @Test
    public void UserService_GetUserById_ThrowsResourceNotFoundException() {
        when(userRepository.findById(user.getId()))
                .thenThrow(new ResourceNotFoundException("User not found with id=" + user.getId()));

        Assertions.assertThatThrownBy(() -> userService.getUserById(user.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id=" + user.getId());
    }

    @Test
    public void UserService_UpdateUserProfile_ReturnUpdatedUser() {
        User newUser = User.builder().id(1L).username("newUsername").build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(newUser);

        User expectedUpdatedUser = userService.updateUserProfile(newUser, 1L);

        Assertions.assertThat(expectedUpdatedUser).isNotNull();
        Assertions.assertThat(expectedUpdatedUser.getUsername()).isEqualTo(newUser.getUsername());
    }

    // TODO test method for updateUsersPassword

    @Test
    public void UserService_DeleteById_ReturnNothing() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        doNothing().when(userRepository).deleteById(id);

        assertAll(() -> userService.deleteUser(id));
    }

    @Test
    public void UserService_GetNumberOfUsers_ReturnNumberOfUsers() {
        when(userRepository.countByRole(Role.USER)).thenReturn(1L);

        Long expectedNumberOfUsers = userService.getNumberOfUsers();

        Assertions.assertThat(expectedNumberOfUsers).isEqualTo(1L);
    }

    @Test
    public void UserService_GetNumberOfUsersAndManagers_ReturnNumberOfUsersAndManagers() {
        User manager = User.builder().id(1L).username("manager").password("manager").role(Role.MANAGER).enabled(true).build();

        when(userRepository.countByRole(Role.USER)).thenReturn(0L);
        when(userRepository.countByRole(Role.MANAGER)).thenReturn(1L);

        Long expectedNumberOfUsersAndManagers = userService.getNumberOfUsersAndManagers();

        Assertions.assertThat(expectedNumberOfUsersAndManagers).isEqualTo(1L);
    }

    @Test
    public void UserService_GetTop3LastUsers_ReturnTop3LastAddedUsers() {
        User user2 = User.builder().id(1L).username("user2").password("user2").role(Role.USER).enabled(true).build();
        User user3 = User.builder().id(1L).username("user3").password("user3").role(Role.USER).enabled(true).build();
        User user4 = User.builder().id(1L).username("user4").password("user4").role(Role.USER).enabled(true).build();

        when(userRepository.findTop3ByRoleOrderByIdDesc(Role.USER)).thenReturn(List.of(user4, user3, user2));

        List<User> expectedUsers = userService.getLast3Users();

        Assertions.assertThat(expectedUsers).isNotNull();
        Assertions.assertThat(expectedUsers.size()).isEqualTo(3);
    }

    @Test
    public void UserService_GetTop3LastManagers_ReturnTop3LastAddedManagers() {
        User manager1 = User.builder().id(1L).username("manager1").password("manager1").role(Role.MANAGER).enabled(true).build();
        User manager2 = User.builder().id(2L).username("manager2").password("manager2").role(Role.MANAGER).enabled(true).build();
        User manager3 = User.builder().id(3L).username("manager3").password("manager3").role(Role.MANAGER).enabled(true).build();

        when(userRepository.findTop3ByRoleOrderByIdDesc(Role.MANAGER)).thenReturn(List.of(manager3, manager2, manager1));

        List<User> expectedManagers = userService.getLast3Managers();

        Assertions.assertThat(expectedManagers).isNotNull();
        Assertions.assertThat(expectedManagers.size()).isEqualTo(3);
    }

    @Test
    public void UserService_UserAlreadyExists_ReturnTrueIfGivenUserAlreadyExists() {
        when(userRepository.exists(Mockito.any(Example.class))).thenReturn(true);

        Assertions.assertThat(userService.userAlreadyExists(user)).isTrue();
    }
}
