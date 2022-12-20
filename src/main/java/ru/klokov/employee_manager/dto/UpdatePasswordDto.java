package ru.klokov.employee_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdatePasswordDto {
    @NotBlank(message = "Current password must be not null and not empty")
    @Size(min = 3, message = "Current password size must be greater than 3")
    private String currentPassword;
    @NotBlank(message = "Current password must be not null and not empty")
    @Size(min = 3, message = "Current password size must be greater than 3")
    private String newPassword;
}
