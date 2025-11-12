package cz.upce.fei.redsys.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public final class AuthDto {
    private AuthDto() {}

    public record RegisterRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 3, max = 50, message = "{user.username.size}")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{user.username.pattern}")
            String username,

            @NotBlank(message = "{common.required}")
            @Size(min = 3, max = 50, message = "{user.fullname.size}")
            @Pattern(regexp = "^[a-zA-Z ]+$", message = "{user.fullname.pattern}")
            String fullName,

            @NotBlank(message = "{common.required}")
            @Email(message = "{common.email}")
            String email,

            @NotBlank(message = "{common.required}")
            @Size(min = 6, max = 256, message = "{user.password.size}")
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "{common.required}")
            String identifier,

            @NotBlank(message = "{common.required}")
            String password
    ) {}

    @Builder
    public record LoginResponse(String token) {}

    public record PasswordResetRequest(
            @NotBlank(message = "{common.required}")
            String identifier
    ) {}

    @Builder
    public record PasswordResetCodeResponse(String code) {}

    public record PasswordResetConfirmRequest(
            @NotBlank(message = "{common.required}")
            String code,

            @NotBlank(message = "{common.required}")
            @Size(min = 6, max = 256, message = "{user.password.size}")
            String newPassword
    ) {}

    public record PasswordChangeRequest(
            @NotBlank(message = "{common.required}")
            String oldPassword,

            @NotBlank(message = "{common.required}")
            @Size(min = 6, max = 256, message = "{user.password.size}")
            String newPassword
    ) {}

    @Builder
    public record MessageResponse(String message) {}
}