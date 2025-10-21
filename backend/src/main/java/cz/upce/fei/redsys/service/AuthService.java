package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.PasswordResetToken;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.AuthDto.*;
import cz.upce.fei.redsys.dto.UserDto;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.repository.PasswordResetTokenRepository;
import cz.upce.fei.redsys.security.TokenProvider;
import lombok.RequiredArgsConstructor;
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
        String hashedPassword = passwordEncoder.encode(req.password());
        User user = userService.createUser(req.username(), req.email(), hashedPassword);
        return UserDto.toUserResponse(user);
    }

    public String login(LoginRequest req) {
        String username = resolveUsernameFromIdentifier(req.identifier());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, req.password())
        );
        return tokenProvider.createToken(auth);
    }

    @Transactional
    public String requestPasswordReset(PasswordResetRequest req) {
        User user = userService.requireUserByIdentifier(req.identifier());
        passwordResetTokenRepository.deleteByUserAndExpiresAtBefore(user, Instant.now());

        String code = codeGenerator.generate();
        PasswordResetToken token = PasswordResetToken.builder()
                .code(code)
                .user(user)
                .expiresAt(Instant.now().plus(RESET_CODE_TTL))
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);

        return code;
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest req) {
        PasswordResetToken token = passwordResetTokenRepository.findByCode(req.code())
                .orElseThrow(() -> new ValidationException("Invalid code"));
        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ValidationException("Code expired or already used");
        }

        User user = token.getUser();
        userService.updatePassword(user, passwordEncoder.encode(req.newPassword()));
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest req) {
        User user = currentUser();
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }
        userService.updatePassword(user, passwordEncoder.encode(req.newPassword()));
    }

    public User currentUser() {
        return userService.findByUsername(currentUsername())
                .orElseThrow(() -> new AccessDeniedException("Not authenticated"));
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
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
