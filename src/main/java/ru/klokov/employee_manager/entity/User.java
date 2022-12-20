package ru.klokov.employee_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.klokov.employee_manager.dto.UserDto;

@Entity
@Table(name = "users")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @Column(name = "enabled")
    private Boolean enabled;

    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
        this.role = userDto.getRole();
        this.enabled = userDto.getEnabled();
    }
}
