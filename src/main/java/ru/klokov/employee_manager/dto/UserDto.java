package ru.klokov.employee_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private Long id;
    @NotBlank(message = "username must be not null and not empty")
    @Size(min = 2, max = 100, message = "username size must be between 2 and 100")
    private String username;
    @NotBlank(message = "password must be not null and not empty")
    @Size(min = 2, max = 100, message = "password size must be between 2 and 100")
    private String password;
    private Role role;
    private Boolean enabled;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.getEnabled();
    }
}
