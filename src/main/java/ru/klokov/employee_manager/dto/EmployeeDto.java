package ru.klokov.employee_manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.klokov.employee_manager.entity.Employee;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmployeeDto {
    private Long id;
    @NotBlank(message = "firstName must be not null and not empty")
    @Size(min = 2, max = 100, message = "firstName size must be between 2 and 100")
    private String firstName;
    @NotBlank(message = "lastName must be not null and not empty")
    @Size(min = 2, max = 100, message = "lastName size must be between 2 and 100")
    private String lastName;
    @NotNull(message = "birthDate must be not null and not empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    @NotNull(message = "employmentDate must be not null and not empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date employmentDate;
    @NotNull(message = "position must be not null and not empty")
    @Min(value = 1L, message = "position id must greater than 1")
    private Long positionId;
    @NotNull(message = "department must be not null and not empty")
    @Min(value = 1L, message = "department id must greater than 1")
    private Long departmentId;

    public EmployeeDto(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.birthDate = employee.getBirthDate();
        this.employmentDate = employee.getEmploymentDate();
        this.positionId = employee.getPosition().getId();
        this.departmentId = employee.getDepartment().getId();
    }
}
