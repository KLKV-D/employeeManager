package ru.klokov.employee_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.entity.Role;
import ru.klokov.employee_manager.entity.User;
import ru.klokov.employee_manager.service.impl.PositionServiceImpl;
import ru.klokov.employee_manager.service.impl.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
public class PositionControllerTest {
    @MockBean
    private PositionServiceImpl positionService;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Position position;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).username("admin").password("admin").role(Role.ADMIN).build();
        position = Position.builder().id(1L).name("position").build();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionService_GetAllPositions_ReturnAllPositions() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.getPositionsPaginated(1, 5, "id", "asc"))
                .thenReturn(Mockito.mock(Page.class));

        this.mockMvc.perform(get("/api/positions/page/{pageNumber}", 1)
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("position/all-positions"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("sortField"))
                .andExpect(model().attributeExists("sortDirection"))
                .andExpect(model().attributeExists("reverseSortDirection"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_GetPositionById_ReturnPositionById() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.getPositionById(1L)).thenReturn(position);

        this.mockMvc.perform(get("/api/positions/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("position/one-position"))
                .andExpect(model().attributeExists("position"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_AddPositionForm_ReturnAddPositionForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);

        this.mockMvc.perform(get("/api/positions/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("position/add-position"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("position"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_AddPosition_ReturnAddedPosition() throws Exception {
        Position position2 = Position.builder().id(2L).name("position2").build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.addPosition(position2)).thenReturn(position2);

        this.mockMvc.perform(post("/api/positions/add")
                        .flashAttr("position", position2).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/positions/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_EditPositionForm_ReturnEditPositionForm() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.getPositionById(1L)).thenReturn(position);

        this.mockMvc.perform(get("/api/positions/edit/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("position/edit-position"))
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(model().attributeExists("position"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_EditPosition_ReturnEditPosition() throws Exception {
        Position newPosition = Position.builder().id(2L).name("position2").build();

        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(positionService.updatePosition(newPosition, 1L)).thenReturn(newPosition);

        this.mockMvc.perform(post("/api/positions/edit/{id}", 1)
                        .flashAttr("position", newPosition).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/positions/page/1?sortField=id&sortDirection=asc"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
    public void PositionController_DeletePosition_ReturnNothing() throws Exception {
        this.mockMvc.perform(get("/api/positions/delete/{id}", 1L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/api/positions/page/1?sortField=id&sortDirection=asc"));
    }
}
