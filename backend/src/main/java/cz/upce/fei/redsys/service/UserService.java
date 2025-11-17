package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating '{}'", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            log.warn("User '{}' is inactive", username);
            throw new UsernameNotFoundException("User is inactive");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        log.debug("Authenticated user '{}' as {}", user.getUsername(), user.getRole().name());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByIdentifier(String identifier) {
        log.debug("Finding user by identifier '{}'", identifier);
        if (identifier.contains("@")) return findByEmail(identifier);
        return findByUsername(identifier);
    }

    @Transactional
    public User createUser(String username, String fullName, String email, String hashedPassword) {
        log.debug("Creating user with username '{}'", username);
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username already taken");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }

        User user = User.builder()
                .username(username)
                .fullName(fullName)
                .email(email)
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User {} created with ID {}", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    @Transactional
    public void updatePassword(User user, String hashedPassword) {
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public User requireUserByIdentifier(String identifier) {
        return findByIdentifier(identifier)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
}