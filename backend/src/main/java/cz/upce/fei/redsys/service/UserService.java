package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is inactive");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

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
        if (identifier.contains("@")) return findByEmail(identifier);
        return findByUsername(identifier);
    }

    @Transactional
    public User createUser(String username, String fullName, String email, String hashedPassword) {
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
        return userRepository.save(user);
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