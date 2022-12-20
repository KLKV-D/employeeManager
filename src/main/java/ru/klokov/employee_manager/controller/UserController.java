package ru.klokov.employee_manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.klokov.employee_manager.dto.UpdatePasswordDto;
import ru.klokov.employee_manager.dto.UserDto;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.exception.UpdatePasswordException;
import ru.klokov.employee_manager.service.UserService;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${page.size}")
    private Integer pageSize;

    @GetMapping("/page/{pageNumber}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String getAllUsersPaginated(@PathVariable("pageNumber") Integer pageNumber,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDirection") String sortDirection,
                                       Model model) {

        User authenticatedUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Role currentUserRole = authenticatedUser.getRole();

        Page<User> page = null;

        if (currentUserRole.equals(Role.MANAGER)) {
            page = userService.getUsersWithUserRolePaginated(pageNumber, pageSize, sortField, sortDirection);
        } else if (currentUserRole.equals(Role.ADMIN)) {
            page = userService.getUsersWithAllRolesPaginated(pageNumber, pageSize, sortField, sortDirection);
        }

        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("users", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "user/all-users";
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or @userServiceImpl.getUserById(#id).getUsername() == authentication.name or hasAuthority('ADMIN')")
    public String getUserProfile(@PathVariable("id") Long id, Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("user", userService.getUserById(id));
        return "user/user-profile";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showAddUserForm(Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("userDto", new UserDto());
        return "user/add-user";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String addUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            return "user/add-user";
        }
        userService.addUser(new User(userDto));
        return "redirect:/api/users/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/edit/profile/{id}")
    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or @userServiceImpl.getUserById(#id).getUsername() == authentication.name or hasAuthority('ADMIN')")
    public String showUpdateUsersProfileForm(@PathVariable("id") Long id, Model model) {
        UserDto userDto = new UserDto(userService.getUserById(id));

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("userDto", userDto);
        return "user/edit-users-profile";
    }

    @PostMapping("/edit/profile/{id}")
    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or @userServiceImpl.getUserById(#id).getUsername() == authentication.name or hasAuthority('ADMIN')")
    public String updateUsersProfile(@PathVariable("id") Long id,
                                     @ModelAttribute("userDto") @Valid UserDto userDto,
                                     BindingResult result,
                                     Model model) {

        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            model.addAttribute("userDto", userDto);
            return "user/edit-users-profile";
        }

        User user = new User(userDto);
        userService.updateUserProfile(user, id);
        return "redirect:/api/home";
    }

    @GetMapping("/edit/password/{id}")
    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or @userServiceImpl.getUserById(#id).getUsername() == authentication.name or hasAuthority('ADMIN')")
    public String showUpdateUsersPasswordForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("updatePasswordDto", new UpdatePasswordDto());
        return "user/edit-users-password";
    }

    @PostMapping("/edit/password/{id}")
    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or @userServiceImpl.getUserById(#id).getUsername() == authentication.name or hasAuthority('ADMIN')")
    public String updateUsersPassword(@PathVariable("id") Long id,
                                      @ModelAttribute("updatePasswordDto") @Valid UpdatePasswordDto updatePasswordDto,
                                      BindingResult result,
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            model.addAttribute("user", userService.getUserById(id));
            model.addAttribute("updatePasswordDto", updatePasswordDto);
            return "user/edit-users-password";
        }

        userService.updateUsersPassword(updatePasswordDto, id);
        return "redirect:/api/home";
    }

    @PreAuthorize("@userServiceImpl.getUserById(#id).getRole().name() == 'USER' and hasAuthority('MANAGER') " +
            "or hasAuthority('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/api/users/page/1?sortField=id&sortDirection=asc";
    }

    @ExceptionHandler({ ResourceNotFoundException.class, UpdatePasswordException.class, ResourceAlreadyExistsException.class })
    public String showErrorPage(HttpServletRequest req, Exception ex, Model model) {
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
