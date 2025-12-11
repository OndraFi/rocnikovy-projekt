package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.dto.UserDto.PaginatedUserResponse;
import cz.upce.fei.redsys.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_ShouldReturnPaginatedUsers() throws Exception {
        PaginatedUserResponse response = new PaginatedUserResponse(
                List.of(new UserResponse(1L, "john", "John Doe", null)),
                0, 20, 1, 1
        );

        when(userService.list(any(Pageable.class)))
                .thenReturn(response);

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].id").value(1))
                .andExpect(jsonPath("$.users[0].username").value("john"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void block_ShouldReturnBlockedUser() throws Exception {
        UserResponse blocked = new UserResponse(1L, "john", "John Doe", null);

        when(userService.blockUser("john"))
                .thenReturn(blocked);

        mvc.perform(patch("/api/users/john/block")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unblock_ShouldReturnUnblockedUser() throws Exception {
        UserResponse unblocked = new UserResponse(1L, "john", "John Doe", null);

        when(userService.unblockUser("john"))
                .thenReturn(unblocked);

        mvc.perform(patch("/api/users/john/unblock")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john"));
    }
}
