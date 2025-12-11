package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.repository.UserRepository;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USERNAME = "john";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "hashedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .id(1L)
                .username(USERNAME)
                .fullName("John Doe")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.USER)
                .active(true)
                .build();

        inactiveUser = User.builder()
                .id(2L)
                .username("inactive")
                .fullName("Inactive User")
                .email("inactive@example.com")
                .password("pwd")
                .role(UserRole.USER)
                .active(false)
                .build();
    }

    // loadUserByUsername

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExistsAndActive() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(activeUser));

        UserDetails details = userService.loadUserByUsername(USERNAME);

        assertNotNull(details);
        assertEquals(USERNAME, details.getUsername());
        assertEquals(PASSWORD, details.getPassword());

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(
                authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + activeUser.getRole().name()))
        );

        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFound_WhenUserDoesNotExist() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(USERNAME));

        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFound_WhenUserInactive() {
        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(inactiveUser));

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("inactive"));

        verify(userRepository, times(1)).findByUsername("inactive");
    }

    // findByUsername

    @Test
    void findByUsername_ShouldReturnOptionalUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(activeUser));

        Optional<User> result = userService.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    // findByEmail

    @Test
    void findByEmail_ShouldReturnOptionalUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));

        Optional<User> result = userService.findByEmail(EMAIL);

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    // findByIdentifier

    @Test
    void findByIdentifier_ShouldUseEmail_WhenIdentifierContainsAt() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));

        Optional<User> result = userService.findByIdentifier(EMAIL);

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void findByIdentifier_ShouldUseUsername_WhenIdentifierWithoutAt() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(activeUser));

        Optional<User> result = userService.findByIdentifier(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).findByEmail(anyString());
    }

    // createUser

    @Test
    void createUser_ShouldCreateAndReturnUser_WhenUsernameAndEmailFree() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(100L);
            return u;
        });

        User result = userService.createUser(USERNAME, "John Doe", EMAIL, PASSWORD);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(USERNAME, result.getUsername());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(PASSWORD, result.getPassword());
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowIllegalState_WhenUsernameTaken() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(activeUser));

        assertThrows(IllegalStateException.class,
                () -> userService.createUser(USERNAME, "John Doe", EMAIL, PASSWORD));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowIllegalState_WhenEmailTaken() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));

        assertThrows(IllegalStateException.class,
                () -> userService.createUser(USERNAME, "John Doe", EMAIL, PASSWORD));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userRepository, never()).save(any());
    }

    // updatePassword

    @Test
    void updatePassword_ShouldSetNewPasswordAndSaveUser() {
        String newHashedPassword = "newHashed";

        userService.updatePassword(activeUser, newHashedPassword);

        assertEquals(newHashedPassword, activeUser.getPassword());
        verify(userRepository, times(1)).save(activeUser);
    }

    // requireUserByIdentifier

    @Test
    void requireUserByIdentifier_ShouldReturnUser_WhenFoundByUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(activeUser));

        User result = userService.requireUserByIdentifier(USERNAME);

        assertNotNull(result);
        assertEquals(activeUser, result);
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void requireUserByIdentifier_ShouldReturnUser_WhenFoundByEmail() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));

        User result = userService.requireUserByIdentifier(EMAIL);

        assertNotNull(result);
        assertEquals(activeUser, result);
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void requireUserByIdentifier_ShouldThrowAccessDenied_WhenUserNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> userService.requireUserByIdentifier(USERNAME));
    }

    // blockUser

    @Test
    void blockUser_ShouldBlockActiveUser() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(activeUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(activeUser);

        UserResponse result = userService.blockUser("john");

        assertEquals(1L, result.id());
        assertFalse(activeUser.isActive());
        verify(userRepository).save(activeUser);
    }

    @Test
    void blockUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> userService.blockUser("missing"));
    }

    // unblockUser

    @Test
    void unblockUser_ShouldUnblockInactiveUser() {
        when(userRepository.findByUsername("inactive"))
                .thenReturn(Optional.of(inactiveUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(inactiveUser);

        UserResponse result = userService.unblockUser("inactive");

        assertEquals(2L, result.id());
        assertTrue(inactiveUser.isActive());
        verify(userRepository).save(inactiveUser);
    }
}
