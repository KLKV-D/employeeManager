package ru.klokov.employee_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.service.DepartmentService;
import ru.klokov.employee_manager.service.EmployeeService;
import ru.klokov.employee_manager.service.PositionService;
import ru.klokov.employee_manager.service.UserService;

@Controller
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @GetMapping
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userService.getUserByUsername(authentication.getName());

        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("users", userService.getLast3Users());
        model.addAttribute("managers", userService.getLast3Managers());
        model.addAttribute("positions", positionService.get3LastPositions());
        model.addAttribute("departments", departmentService.get3LastDepartments());
        model.addAttribute("employees", employeeService.get3LastEmployees());
        model.addAttribute("numberOfUsers", userService.getNumberOfUsers());
        model.addAttribute("numberOfUsersAndManagers", userService.getNumberOfUsersAndManagers());
        model.addAttribute("numberOfPositions", positionService.getNumberOfPositions());
        model.addAttribute("numberOfDepartments", departmentService.getNumberOfDepartments());
        model.addAttribute("numberOfEmployees", employeeService.getNumberOfEmployees());

        return "home";
    }
}
