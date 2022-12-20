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
import ru.klokov.employee_manager.entity.Department;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.exception.UpdatePasswordException;
import ru.klokov.employee_manager.service.DepartmentService;
import ru.klokov.employee_manager.service.UserService;

@Controller
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final UserService userService;
    @Value("${page.size}")
    private Integer pageSize;

    @GetMapping("/{id}")
    public String getOneDepartment(@PathVariable("id") Long id, Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "department/one-department";
    }

    @GetMapping("/add")
    public String showAddDepartmentForm(Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("department", new Department());
        return "department/add-department";
    }

    @PostMapping("/add")
    public String addDepartment(@ModelAttribute("department") @Valid Department department,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            return "department/add-department";
        }
        departmentService.addDepartment(department);
        return "redirect:/api/departments/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/edit/{id}")
    public String showEditDepartmentForm(@PathVariable("id") Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("department", department);
        return "department/edit-department";
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(@PathVariable("id") Long id,
                                   @ModelAttribute("department") @Valid Department department,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            return "department/edit-department";
        }
        departmentService.updateDepartment(department, id);
        return "redirect:/api/departments/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/api/departments/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/page/{pageNumber}")
    public String getAllDepartmentsPaginated(@PathVariable("pageNumber") int pageNumber,
                                             @RequestParam("sortField") String sortField,
                                             @RequestParam("sortDirection") String sortDirection,
                                             Model model) {
        Page<Department> page = departmentService.getDepartmentsPaginated(pageNumber, pageSize, sortField, sortDirection);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("departments", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "department/all-departments";
    }

    @ExceptionHandler({ ResourceNotFoundException.class, ResourceAlreadyExistsException.class })
    public String showErrorPage(HttpServletRequest req, Exception ex, Model model) {
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
