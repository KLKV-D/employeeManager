package ru.klokov.employee_manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.klokov.employee_manager.dto.UpdatePasswordDto;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.exception.UpdatePasswordException;
import ru.klokov.employee_manager.repository.UserRepository;
import ru.klokov.employee_manager.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getUsersWithAllRolesPaginated(Integer pageNumber, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getUsersWithUserRolePaginated(Integer pageNumber, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        User user = new User();
        user.setRole(Role.USER);

        return userRepository.findAll(Example.of(user), pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) throw new ResourceAlreadyExistsException(
                "User with username " + user.getUsername() + " already exists!");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUserProfile(User user, Long id) {
        if (userAlreadyExists(user)) throw new ResourceAlreadyExistsException(
                "User with username " + user.getUsername() + " already exists!");

        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + id));
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setRole(user.getRole());
        userToUpdate.setEnabled(user.getEnabled());
        return userRepository.save(userToUpdate);
    }

    @Override
    public User updateUsersPassword(UpdatePasswordDto updatePasswordDto, Long id) {
        User userToUpdatePassword = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + id));

        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), userToUpdatePassword.getPassword())) {
            throw new UpdatePasswordException("Passwords doesn't match!");
        }

        userToUpdatePassword.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        return userRepository.save(userToUpdatePassword);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + id));
        userRepository.deleteById(id);
    }

    @Override
    public Long getNumberOfUsers() {
        return userRepository.countByRole(Role.USER);
    }

    @Override
    public Long getNumberOfUsersAndManagers() {
        return userRepository.countByRole(Role.USER) + userRepository.countByRole(Role.MANAGER);
    }

    @Override
    public List<User> getLast3Users() {
        return userRepository.findTop3ByRoleOrderByIdDesc(Role.USER);
    }

    @Override
    public List<User> getLast3Managers() {
        return userRepository.findTop3ByRoleOrderByIdDesc(Role.MANAGER);
    }

    @Override
    public Boolean userAlreadyExists(User user) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withIgnorePaths("password")
                .withIgnorePaths("enabled")
                .withIgnorePaths("role")
                .withMatcher("username", ignoreCase());
        Example<User> example = Example.of(user, matcher);
        return userRepository.exists(example);
    }
}
