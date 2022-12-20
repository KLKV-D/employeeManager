package ru.klokov.employee_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.klokov.employee_manager.dto.EmployeeDto;
import java.util.Date;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "employment_date")
    private Date employmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    public Employee(EmployeeDto employeeDto) {
        this.firstName = employeeDto.getFirstName();
        this.lastName = employeeDto.getLastName();
        this.birthDate = employeeDto.getBirthDate();
        this.employmentDate = employeeDto.getEmploymentDate();
    }
}
