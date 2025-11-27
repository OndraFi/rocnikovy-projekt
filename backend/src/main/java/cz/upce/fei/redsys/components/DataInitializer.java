package cz.upce.fei.redsys.components;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.ArticleDto.CreateArticleRequest;
import cz.upce.fei.redsys.repository.CategoryRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import cz.upce.fei.redsys.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ArticleService articleService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data init running...");

        // --- Admin user ---
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    String hashedPassword = passwordEncoder.encode("heslo123");
                    User u = User.builder()
                            .username("admin")
                            .fullName("admin user")
                            .email("admin@gmail.com")
                            .password(hashedPassword)
                            .role(UserRole.ADMIN)
                            .build();
                    log.info("Creating user {}", u.getEmail());
                    return userRepository.save(u);
                });

        // --- Categories ---
        Category sport = categoryRepository.findByName("Sport")
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Sport");
                    c.setDescription("Sport related items");
                    log.info("Creating category {}", c.getName());
                    return categoryRepository.save(c);
                });

        Category ekonomika = categoryRepository.findByName("Ekonomika")
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Ekonomika");
                    c.setDescription("Ekonomika related items");
                    log.info("Creating category {}", c.getName());
                    return categoryRepository.save(c);
                });

        Set<Long> categoryIds = Set.of(sport.getId(), ekonomika.getId());

        // --- Inicializace článků přes ArticleService ---
        if (articleService.list(org.springframework.data.domain.PageRequest.of(0,1)).getTotalElements() == 0) {
            log.info("Creating default articles via ArticleService...");

            // 1) Football News
            CreateArticleRequest footballReq = new CreateArticleRequest(
                    "Football News",
                    ArticleState.PUBLISHED,
                    Instant.now(),
                    "Obsah článku Football News",
                    categoryIds,
                    null // editorUsername
            );
            articleService.create(footballReq);

            // 2) Série Ekonomy News
            for (int i = 1; i <= 5; i++) {
                CreateArticleRequest ecoReq = new CreateArticleRequest(
                        "Ekonomy News " + i,
                        ArticleState.PUBLISHED,
                        Instant.now(),
                        "Obsah článku Ekonomy News " + i,
                        categoryIds,
                        null
                );
                articleService.create(ecoReq);
            }
        }

        log.info("Data init finished.");
    }
}
