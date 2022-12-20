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
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.service.PositionService;
import ru.klokov.employee_manager.service.UserService;

@Controller
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;
    private final UserService userService;
    @Value("${page.size}")
    private Integer pageSize;

    @GetMapping("/{id}")
    public String getOnePosition(@PathVariable("id") Long id, Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("position", positionService.getPositionById(id));
        return "position/one-position";
    }

    @GetMapping("/add")
    public String showAddPositionForm(Model model) {
        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("position", new Position());
        return "position/add-position";
    }

    @PostMapping("/add")
    public String addPosition(@ModelAttribute("position") @Valid Position position,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            return "position/add-position";
        }
        positionService.addPosition(position);
        return "redirect:/api/positions/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/edit/{id}")
    public String showEditPositionForm(@PathVariable("id") Long id, Model model) {
        Position position = positionService.getPositionById(id);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("position", position);
        return "position/edit-position";
    }

    @PostMapping("/edit/{id}")
    public String updatePosition(@ModelAttribute("position") @Valid Position position,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                    .getAuthentication().getName()));
            return "position/edit-position";
        }
        positionService.updatePosition(position, position.getId());
        return "redirect:/api/positions/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/delete/{id}")
    public String deletePosition(@PathVariable("id") Long id) {
        positionService.deletePosition(id);
        return "redirect:/api/positions/page/1?sortField=id&sortDirection=asc";
    }

    @GetMapping("/page/{pageNumber}")
    public String getAllPositionsPaginated(@PathVariable("pageNumber") int pageNumber,
                                           @RequestParam("sortField") String sortField,
                                           @RequestParam("sortDirection") String sortDirection,
                                           Model model) {
        Page<Position> page = positionService.getPositionsPaginated(pageNumber, pageSize, sortField, sortDirection);

        model.addAttribute("authenticatedUser", userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("positions", page.getContent());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "position/all-positions";
    }

    @ExceptionHandler({ ResourceNotFoundException.class, ResourceAlreadyExistsException.class })
    public String showErrorPage(HttpServletRequest req, Exception ex, Model model) {
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
