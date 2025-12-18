package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.AuthDto.*;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AuthControllerTest {

    private static final String API_BASE = "/api/auth";
    private static final String TEST_USER = "testUser";
    private static final String TEST_FULLNAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASS = "securePassword";
    private static final UserRole TEST_ROLE =  UserRole.USER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_ShouldReturnUserAnd200() throws Exception {
        RegisterRequest request = new RegisterRequest(TEST_USER, TEST_FULLNAME,TEST_EMAIL, TEST_PASS);
        UserResponse mockUserResponse = new UserResponse(1L, TEST_USER, TEST_FULLNAME, TEST_ROLE, true);

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockUserResponse);

        mockMvc.perform(post(API_BASE + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.fullName").value(TEST_FULLNAME));
    }

    @Test
    void login_ShouldReturnTokenAnd200() throws Exception {
        LoginRequest request = new LoginRequest(TEST_USER, TEST_PASS);
        String mockToken = "mock.jwt.token";

        when(authService.login(any(LoginRequest.class))).thenReturn(mockToken);

        mockMvc.perform(post(API_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void requestReset_ShouldReturnResetCodeAnd200() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest(TEST_EMAIL);
        String mockCode = "ABC123";

        when(authService.requestPasswordReset(any(PasswordResetRequest.class))).thenReturn(mockCode);

        mockMvc.perform(post(API_BASE + "/request-password-reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(mockCode));
    }

    @Test
    void confirmReset_ShouldReturnSuccessMessageAnd200() throws Exception {
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("ABC123", "newPassword");

        doNothing().when(authService).confirmPasswordReset(any(PasswordResetConfirmRequest.class));

        mockMvc.perform(post(API_BASE + "/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful"));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void changePassword_ShouldReturnSuccessMessageAnd200() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword");

        doNothing().when(authService).changePassword(any(PasswordChangeRequest.class));

        mockMvc.perform(post(API_BASE + "/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed"));
    }
}