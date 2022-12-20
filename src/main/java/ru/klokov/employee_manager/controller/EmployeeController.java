package ru.klokov.employee_manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.klokov.employee_manager.dto.EmployeeDto;
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.entity.Employee;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.service.DepartmentService;
import ru.klokov.employee_manager.service.EmployeeService;
import ru.klokov.employee_manager.service.PositionService;
import ru.klokov.employee_manager.service.UserService;

@Controller
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final UserService userService;
    @Value("${page.size}")
    private Integer pageSize;

    @GetMapping("/{id}")
    public String getOneEmployee(@PathVariable("id") Long id, Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employee/one-employee";
    }

    @GetMapping("/add")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employeeDto", new EmployeeDto());
        model.addAttribute("positions", positionService.getAllPositions());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employee/add-employee";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute("employeeDto") @Valid EmployeeDto employeeDto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            model.addAttribute("positions", positionService.getAllPositions());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "employee/add-employee";
        }

        Position position = positionService.getPositionById(employeeDto.getPositionId());
        Department department = departmentService.getDepartmentById(employeeDto.getDepartmentId());
        Employee employee = new Employee(employeeDto);
        employee.setPosition(position);
        employee.setDepartment(department);
        employeeService.addEmployee(employee);
        return "redirect:/api/employees/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/edit/{id}")
    public String showEditEmployeeForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employeeDto", new EmployeeDto(employee));
        model.addAttribute("positions", positionService.getAllPositions());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employee/edit-employee";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @ModelAttribute("employeeDto") @Valid EmployeeDto employeeDto,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            model.addAttribute("positions", positionService.getAllPositions());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "employee/edit-employee";
        }
        Position position = positionService.getPositionById(employeeDto.getPositionId());
        Department department = departmentService.getDepartmentById(employeeDto.getDepartmentId());
        Employee employee = new Employee(employeeDto);
        employee.setPosition(position);
        employee.setDepartment(department);
        employeeService.updateEmployee(employee, id);
        return "redirect:/api/employees/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/api/employees/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/page/{pageNumber}")
    public String getAllEmployeesPaginated(@PathVariable("pageNumber") int pageNumber,
                                           @RequestParam("sortField") String sortField,
                                           @RequestParam("sortDirection") String sortDirection,
                                           Model model) {

        Page<Employee> page = employeeService.getEmployeesPaginated(pageNumber, pageSize, sortField, sortDirection);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employees", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "employee/all-employees";
    }

    @GetMapping("/position/{positionId}/page/{pageNumber}")
    public String getEmployeesByPosition(@PathVariable("positionId") Long positionId,
                                         @PathVariable("pageNumber") int pageNumber,
                                         @RequestParam("sortField") String sortField,
                                         @RequestParam("sortDirection") String sortDirection,
                                         Model model) {

        Position position = positionService.getPositionById(positionId);

        Page<Employee> page = employeeService.getEmployeesByPosition(position, pageNumber, pageSize, sortField, sortDirection);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employees", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "employee/all-employees";
    }

    @GetMapping("/department/{departmentId}/page/{pageNumber}")
    public String getEmployeesByDepartment(@PathVariable("departmentId") Long departmentId,
                                           @PathVariable("pageNumber") int pageNumber,
                                           @RequestParam("sortField") String sortField,
                                           @RequestParam("sortDirection") String sortDirection,
                                           Model model) {
        Department department = departmentService.getDepartmentById(departmentId);

        Page<Employee> page = employeeService.getEmployeesByDepartment(department, pageNumber, pageSize, sortField, sortDirection);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("employees", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "employee/all-employees";
    }

    @ExceptionHandler({ ResourceNotFoundException.class, ResourceAlreadyExistsException.class })
    public String showErrorPage(HttpServletRequest req, Exception ex, Model model) {
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
