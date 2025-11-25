package cz.upce.fei.redsys.components;

import ch.qos.logback.classic.encoder.JsonEncoder;
import cz.upce.fei.redsys.domain.Category;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.repository.CategoryRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import cz.upce.fei.redsys.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data init running...");
        String hashedPassword = passwordEncoder.encode("heslo123");
        User user = User.builder()
                .username("admin")
                .fullName("admin user")
                .email("admin@gmail.com")
                .password(hashedPassword)
                .build();
        if(userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating user {}", user.getEmail());
            userRepository.save(user);
        }

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        log.info("First user {}", users.getFirst());

        Category c1 = new Category();
        c1.setName("Sport");
        c1.setDescription("Sport related items");
        if(!categoryRepository.existsByName(c1.getName())) {
            log.info("Creating category {}", c1.getName());
            categoryRepository.save(c1);
        }

        Category c2 = new Category();
        c2.setName("Ekonomika");
        c2.setDescription("Ekonomika related items");
        if(!categoryRepository.existsByName(c2.getName())) {
            log.info("Creating category {}", c2.getName());
            categoryRepository.save(c2);
        }
    }

}
