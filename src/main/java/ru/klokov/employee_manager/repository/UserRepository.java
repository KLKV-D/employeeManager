package ru.klokov.employee_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Long countByRole(Role role);
    List<User> findTop3ByRoleOrderByIdDesc(Role role);
}
