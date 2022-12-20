package ru.klokov.employee_manager.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        User user = User.builder()
                .id(1L)
                .username("user")
                .password("user")
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isEqualTo(user.getId());
        Assertions.assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void UserRepository_FindByUsername_ReturnUserWithGivenUsername() {
        User user = User.builder()
                .id(1L)
                .username("user")
                .password("user")
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        Optional<User> expectedUser = userRepository.findByUsername("user");

        Assertions.assertThat(expectedUser.isPresent()).isEqualTo(true);
        Assertions.assertThat(expectedUser.get().getId()).isEqualTo(user.getId());
        Assertions.assertThat(expectedUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void UserRepository_CountByRole_ReturnNumberOfUsersWithGivenRole() {
        User user1 = User.builder().username("user1").password("user1").role(Role.USER).enabled(true).build();
        User user2 = User.builder().username("user2").password("user2").role(Role.USER).enabled(true).build();
        User user3 = User.builder().username("user3").password("user3").role(Role.USER).enabled(true).build();
        User manager1 = User.builder().username("manager1").password("manager1").role(Role.MANAGER).enabled(true).build();
        User manager2 = User.builder().username("manager2").password("manager2").role(Role.MANAGER).enabled(true).build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(manager1);
        userRepository.save(manager2);

        Long numberOfUsersExpected = userRepository.countByRole(Role.USER);
        Long numberOfManagersExpected = userRepository.countByRole(Role.MANAGER);

        Assertions.assertThat(numberOfUsersExpected).isEqualTo(3L);
        Assertions.assertThat(numberOfManagersExpected).isEqualTo(2L);
    }

    @Test
    public void UserRepository_FoundTop3ByRoleOrderByIdDesc_ReturnTop3UsersWithGivenRoleOrderedByIdDesc() {
        User user1 = User.builder().username("user1").password("user1").role(Role.USER).enabled(true).build();
        User user2 = User.builder().username("user2").password("user2").role(Role.USER).enabled(true).build();
        User user3 = User.builder().username("user3").password("user3").role(Role.USER).enabled(true).build();
        User user4 = User.builder().username("user4").password("user4").role(Role.USER).enabled(true).build();
        User manager1 = User.builder().username("manager1").password("manager1").role(Role.MANAGER).enabled(true).build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(manager1);

        List<User> expectedUsers = userRepository.findTop3ByRoleOrderByIdDesc(Role.USER);

        Assertions.assertThat(expectedUsers.size()).isEqualTo(3);
        Assertions.assertThat(expectedUsers.get(0).getUsername()).isEqualTo("user4");
        Assertions.assertThat(expectedUsers.get(1).getUsername()).isEqualTo("user3");
        Assertions.assertThat(expectedUsers.get(2).getUsername()).isEqualTo("user2");
    }
}
