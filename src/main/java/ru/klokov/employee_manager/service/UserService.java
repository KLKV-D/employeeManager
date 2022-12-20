package ru.klokov.employee_manager.service;

import org.springframework.data.domain.Page;
import ru.klokov.employee_manager.dto.UpdatePasswordDto;
import ru.klokov.employee_manager.entity.User;

import java.util.List;

public interface UserService {
    Page<User> getUsersWithAllRolesPaginated(Integer pageNumber, Integer pageSize, String sortField, String sortDirection);
    Page<User> getUsersWithUserRolePaginated(Integer pageNumber, Integer pageSize, String sortField, String sortDirection);
    User getUserById(Long id);
    User getUserByUsername(String username);
    User addUser(User user);
    User updateUserProfile(User user, Long id);
    User updateUsersPassword(UpdatePasswordDto updatePasswordDto, Long id);
    void deleteUser(Long id);
    Long getNumberOfUsers();
    Long getNumberOfUsersAndManagers();
    List<User> getLast3Users();
    List<User> getLast3Managers();
    Boolean userAlreadyExists(User user);
}
