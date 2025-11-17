package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.PasswordResetToken;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.AuthDto.*;
import cz.upce.fei.redsys.dto.UserDto;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.repository.PasswordResetTokenRepository;
import cz.upce.fei.redsys.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ValidationException;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UUIDCodeGenerator codeGenerator;

    private static final Duration RESET_CODE_TTL = Duration.ofMinutes(10);

    @Transactional
    public UserResponse register(RegisterRequest req) {
        log.debug("Registering user with username '{}' and email '{}'", req.username(), req.email());
        String hashedPassword = passwordEncoder.encode(req.password());
        User user = userService.createUser(req.username(), req.fullName(), req.email(), hashedPassword);
        log.info("User {} registered", user.getUsername());
        return UserDto.toUserResponse(user);
    }

    public String login(LoginRequest req) {
        log.debug("Login attempt for identifier '{}'", req.identifier());
        String username = resolveUsernameFromIdentifier(req.identifier());

        User user = userService.findByIdentifier(username)
                .orElseThrow(() -> new AccessDeniedException("Not authenticated"));

        if (!user.isActive()) {
            log.warn("Login attempt for inactive user '{}'", username);
            throw new AccessDeniedException("User is inactive");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, req.password())
        );

        log.info("User '{}' logged in successfully", username);
        return tokenProvider.createToken(auth);
    }

    @Transactional
    public String requestPasswordReset(PasswordResetRequest req) {
        log.debug("Password reset requested for identifier '{}'", req.identifier());
        User user = userService.requireUserByIdentifier(req.identifier());
        passwordResetTokenRepository.deleteByUserAndExpiresAtBefore(user, Instant.now());
        log.debug("Old password reset tokens deleted for user '{}'", user.getUsername());

        String code = codeGenerator.generate();
        PasswordResetToken token = PasswordResetToken.builder()
                .code(code)
                .user(user)
                .expiresAt(Instant.now().plus(RESET_CODE_TTL))
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);

        log.info("Password reset token created for user '{}'", user.getUsername());
        return code;
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest req) {
        log.debug("Confirming password reset for code '{}'", req.code());
        PasswordResetToken token = passwordResetTokenRepository.findByCode(req.code())
                .orElseThrow(() -> new ValidationException("Invalid code"));
        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Password reset code '{}' is expired or already used", req.code());
            throw new ValidationException("Code expired or already used");
        }

        User user = token.getUser();
        userService.updatePassword(user, passwordEncoder.encode(req.newPassword()));
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        log.info("Password reset successfully for user '{}'", user.getUsername());
    }

    @Transactional
    public void changePassword(PasswordChangeRequest req) {
        User user = currentUser();
        log.debug("Changing password for user '{}'", user.getUsername());
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            log.warn("Password change failed for user '{}': old password does not match", user.getUsername());
            throw new ValidationException("Old password is incorrect");
        }
        userService.updatePassword(user, passwordEncoder.encode(req.newPassword()));
        log.info("Password changed successfully for user '{}'", user.getUsername());
    }

    public User currentUser() {
        String username = currentUsername();
        log.debug("Fetching current authenticated user '{}'", username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Not authenticated"));

        if (!user.isActive()) {
            log.warn("Current user '{}' is inactive", username);
            throw new AccessDeniedException("User is inactive");
        }
        return user;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            log.warn("No authentication found in security context");
            throw new AccessDeniedException("Not authenticated");
        }
        return auth.getName();
    }

    private String resolveUsernameFromIdentifier(String identifier) {
        return userService.findByIdentifier(identifier)
                .map(User::getUsername)
                .orElse(identifier);
    }
}
