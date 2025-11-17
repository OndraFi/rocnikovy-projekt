package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.AuthDto;
import cz.upce.fei.redsys.dto.AuthDto.*;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
})
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and password management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Create a new user with unique username and email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username/email already taken", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        log.debug("POST /api/auth/register: {}", req);
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(summary = "Login", description = "Authenticate a user and return a JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        log.debug("POST /api/auth/login: {}", req);
        String token = authService.login(req);
        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }

    @Operation(summary = "Request password reset", description = "Generate a password reset code for the given identifier (username/email)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset code generated", content = @Content(schema = @Schema(implementation = PasswordResetCodeResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/request-password-reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordResetCodeResponse> requestReset(@Valid @RequestBody PasswordResetRequest req) {
        log.debug("POST /api/auth/request-password-reset: {}", req);
        String code = authService.requestPasswordReset(req);
        return ResponseEntity.ok(PasswordResetCodeResponse.builder().code(code).build());
    }

    @Operation(summary = "Confirm password reset", description = "Reset password using the provided reset code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invalid/expired reset code", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> confirmReset(@Valid @RequestBody PasswordResetConfirmRequest req) {
        log.debug("POST /api/auth/reset-password: {}", req);
        authService.confirmPasswordReset(req);
        return ResponseEntity.ok(MessageResponse.builder().message("Password reset successful").build());
    }

    @Operation(summary = "Change password", description = "Change password for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Old password incorrect", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody PasswordChangeRequest req) {
        log.debug("POST /api/auth/change-password: {}", req);
        authService.changePassword(req);
        return ResponseEntity.ok(MessageResponse.builder().message("Password changed").build());
    }
}