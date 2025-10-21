package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.PasswordResetToken;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.AuthDto.LoginRequest;
import cz.upce.fei.redsys.dto.AuthDto.PasswordChangeRequest;
import cz.upce.fei.redsys.dto.AuthDto.PasswordResetConfirmRequest;
import cz.upce.fei.redsys.dto.AuthDto.PasswordResetRequest;
import cz.upce.fei.redsys.dto.AuthDto.RegisterRequest;
import cz.upce.fei.redsys.repository.PasswordResetTokenRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import cz.upce.fei.redsys.security.TokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final String MOCK_TOKEN = "mock.jwt.token";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private UUIDCodeGenerator codeGenerator;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(ENCODED_PASSWORD)
                .build();
    }

    @Test
    void register_ShouldSaveNewUserAndReturnResponse() {
        RegisterRequest request = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, RAW_PASSWORD);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        authService.register(request);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals(TEST_USERNAME) &&
                        user.getPassword().equals(ENCODED_PASSWORD)
        ));
    }

    @Test
    void register_ShouldThrowIllegalState_WhenUsernameTaken() {
        RegisterRequest request = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, RAW_PASSWORD);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalStateException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken_UsingUsername() {
        LoginRequest request = new LoginRequest(TEST_USERNAME, RAW_PASSWORD);
        Authentication mockAuth = mock(Authentication.class);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(tokenProvider.createToken(mockAuth)).thenReturn(MOCK_TOKEN);

        String token = authService.login(request);

        assertEquals(MOCK_TOKEN, token);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken_UsingEmail() {
        LoginRequest request = new LoginRequest(TEST_EMAIL, RAW_PASSWORD);
        Authentication mockAuth = mock(Authentication.class);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(tokenProvider.createToken(mockAuth)).thenReturn(MOCK_TOKEN);

        String token = authService.login(request);

        assertEquals(MOCK_TOKEN, token);
        verify(authenticationManager).authenticate(argThat(auth ->
                auth.getPrincipal().equals(TEST_USERNAME)
        ));
    }

    @Test
    void requestPasswordReset_ShouldCreateAndSaveToken() {
        PasswordResetRequest request = new PasswordResetRequest(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        String fixedCode = "00000000000000000000000000000001";
        when(codeGenerator.generate()).thenReturn(fixedCode);
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(mockToken);

        String code = authService.requestPasswordReset(request);

        assertEquals(fixedCode, code);
        verify(passwordResetTokenRepository, times(1)).deleteByUserAndExpiresAtBefore(eq(mockUser), any(Instant.class));
        verify(passwordResetTokenRepository, times(1)).save(argThat(token ->
                token.getCode().equals(code) &&
                        token.getUser().equals(mockUser) &&
                        !token.isUsed()
        ));
    }

    @Test
    void requestPasswordReset_ShouldThrowNotFound_WhenUserNotFound() {
        PasswordResetRequest request = new PasswordResetRequest(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.requestPasswordReset(request));
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    void confirmPasswordReset_ShouldUpdatePasswordAndMarkTokenUsed() {
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("valid_code", RAW_PASSWORD);
        PasswordResetToken mockToken = PasswordResetToken.builder()
                .code("valid_code")
                .user(mockUser)
                .expiresAt(Instant.now().plusSeconds(60))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByCode("valid_code")).thenReturn(Optional.of(mockToken));
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn("new_encoded_password");

        authService.confirmPasswordReset(request);

        assertTrue(mockToken.isUsed());
        verify(passwordResetTokenRepository, times(1)).save(mockToken);
        verify(userRepository, times(1)).save(argThat(user ->
                user.getPassword().equals("new_encoded_password")
        ));
    }

    @Test
    void confirmPasswordReset_ShouldThrowValidationException_WhenCodeInvalid() {
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("invalid_code", RAW_PASSWORD);
        when(passwordResetTokenRepository.findByCode("invalid_code")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> authService.confirmPasswordReset(request));
    }

    @Test
    void confirmPasswordReset_ShouldThrowValidationException_WhenCodeExpired() {
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("expired_code", RAW_PASSWORD);
        PasswordResetToken mockToken = PasswordResetToken.builder()
                .code("expired_code")
                .user(mockUser)
                .expiresAt(Instant.now().minusSeconds(60))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByCode("expired_code")).thenReturn(Optional.of(mockToken));

        assertThrows(ValidationException.class, () -> authService.confirmPasswordReset(request));
    }

    private void mockCurrentAuthContext(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    }

    @Test
    void changePassword_ShouldUpdatePassword() {
        PasswordChangeRequest request = new PasswordChangeRequest(RAW_PASSWORD, "new_password");
        mockCurrentAuthContext(TEST_USERNAME);

        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode("new_password")).thenReturn("new_encoded_password");

        authService.changePassword(request);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getPassword().equals("new_encoded_password")
        ));
    }

    @Test
    void changePassword_ShouldThrowValidationException_WhenOldPasswordIncorrect() {
        PasswordChangeRequest request = new PasswordChangeRequest(RAW_PASSWORD, "new_password");
        mockCurrentAuthContext(TEST_USERNAME);

        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(ValidationException.class, () -> authService.changePassword(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldThrowAccessDenied_WhenNotAuthenticated() {
        PasswordChangeRequest request = new PasswordChangeRequest(RAW_PASSWORD, "new_password");

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> authService.changePassword(request));
        verify(userRepository, never()).save(any());
    }
}